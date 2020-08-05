package repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import model.Student;

public class StudentMongoRepository implements StudentRepository{

	private static final String ID = "id";
	private static final String COURSES = "courses";
	private MongoCollection<Document> studentCollection;

	public StudentMongoRepository(MongoClient mongoClient, String dbName, String dbCollection) {
		studentCollection = mongoClient.getDatabase(dbName).getCollection(dbCollection);
	}

	@Override
	public List<Student> findAll(ClientSession clientSession) {
		return StreamSupport.
				stream(studentCollection.find(clientSession).spliterator(), false)
				.map(this::fromDocumentToStudent)
				.collect(Collectors.toList());
	}

	@Override
	public Student findById(ClientSession clientSession, String id) {
		Document document  = studentCollection.find(clientSession, Filters.eq(ID, id)).first();
		if(document != null) {
			return fromDocumentToStudent(document);
		}
		return null;
	}

	@Override
	public void save(ClientSession clientSession, Student student) {
		studentCollection.insertOne(clientSession,
				new Document()
				.append(ID, student.getId())
				.append("name", student.getName())
				.append(COURSES, Collections.emptyList()));
	}

	@Override
	public void delete(ClientSession clientSession, Student student) {
		studentCollection.deleteOne(clientSession, Filters.eq(ID, student.getId()));
	}

	@Override
	public void updateStudentCourses(ClientSession clientSession, String studentId, String courseId) {
		studentCollection.updateOne(clientSession, Filters.eq(ID, studentId), 
				Updates.push(COURSES, courseId));
	}

	@Override
	public void removeStudentCourse(ClientSession clientSession, String studentId, String courseId) {
		studentCollection.updateOne(clientSession, Filters.eq(ID, studentId), 
				Updates.pull(COURSES, courseId));		
	}

	@Override
	public List<String> findStudentCourses(ClientSession clientSession, String studentId) {
		return studentCollection.find(clientSession, Filters.eq(ID, studentId))
				.first().getList(COURSES, String.class);
	}

	private Student fromDocumentToStudent(Document document) {
		return new Student(document.getString(ID), document.getString("name"));
	}

}
