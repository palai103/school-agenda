package repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import model.Student;

public class StudentMongoRepository implements StudentRepository{

	private MongoCollection<Document> studentCollection;

	public StudentMongoRepository(MongoClient mongoClient, String dbName, String dbCollection) {
		studentCollection = mongoClient.getDatabase(dbName).getCollection(dbCollection);
	}

	@Override
	public List<Student> findAll() {
		return StreamSupport.
				stream(studentCollection.find().spliterator(), false)
				.map(this::fromDocumentToStudent)
				.collect(Collectors.toList());
	}

	@Override
	public Student findById(String id) {
		Document document  = studentCollection.find(Filters.eq("id", id)).first();
		if(document != null) {
			return fromDocumentToStudent(document);
		}
		return null;
	}

	@Override
	public void save(Student student) {
		studentCollection.insertOne(
				new Document()
				.append("id", student.getId())
				.append("name", student.getName())
				.append("courses", Collections.emptyList()));

	}

	@Override
	public void delete(Student student) {
		studentCollection.deleteOne(Filters.eq("id", student.getId()));
	}

	@Override
	public void updateStudentCourses(String studentId, String courseId) {
		studentCollection.updateOne(Filters.eq("id", studentId), 
				Updates.push("courses", courseId));
	}

	@Override
	public void removeStudentCourse(String studentId, String courseId) {
		studentCollection.updateOne(Filters.eq("id", studentId), 
				Updates.pull("courses", courseId));		
	}

	@Override
	public List<String> findStudentCourses(String studentId) {
		return studentCollection.find(Filters.eq("id", studentId))
				.first().getList("courses", String.class);
	}

	private Student fromDocumentToStudent(Document document) {
		return new Student(document.getString("id"), document.getString("name"));
	}

}
