package repository;

import java.util.ArrayList;
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

import model.Course;
import model.Student;

public class StudentMongoRepository implements StudentRepository{

	private static final String ID = "id";
	private static final String COURSES = "courses";
	
	private MongoCollection<Document> studentCollection;
	private MongoCollection<Document> courseCollection;
	
	private MongoClient mongoClient;
	private ClientSession clientSession;

	public StudentMongoRepository(MongoClient mongoClient, String dbName, String dbCollection) {
		this.mongoClient = mongoClient;
		this.clientSession = mongoClient.startSession();
		studentCollection = mongoClient.getDatabase(dbName).getCollection(dbCollection);
		courseCollection = mongoClient.getDatabase(dbName).getCollection(COURSES);
	}

	@Override
	public List<Student> findAll() {
		return StreamSupport.
				stream(studentCollection.find(getClientSession()).spliterator(), false)
				.map(this::fromDocumentToStudent)
				.collect(Collectors.toList());
	}

	@Override
	public Student findById(String id) {
		ClientSession clientSession = mongoClient.startSession();
		Document document  = studentCollection.find(clientSession, Filters.eq(ID, id)).first();

		if(document != null) {
			return fromDocumentToStudent(document);
		}
		return null;
	}

	@Override
	public void save(Student student) {
		ClientSession clientSession = mongoClient.startSession();
		studentCollection.insertOne(clientSession,
				new Document()
				.append(ID, student.getId())
				.append("name", student.getName())
				.append(COURSES, Collections.emptyList()));

	}

	@Override
	public void delete(Student student) {
		ClientSession clientSession = mongoClient.startSession();
		studentCollection.deleteOne(clientSession, Filters.eq(ID, student.getId()));

	}

	@Override
	public void updateStudentCourses(String studentId, String courseId) {
		ClientSession clientSession = mongoClient.startSession();
		studentCollection.updateOne(clientSession, Filters.eq(ID, studentId), 
				Updates.push(COURSES, courseId));

	}

	@Override
	public void removeStudentCourse(String studentId, String courseId) {
		ClientSession clientSession = mongoClient.startSession();
		studentCollection.updateOne(clientSession, Filters.eq(ID, studentId), 
				Updates.pull(COURSES, courseId));

	}

	@Override
	public List<Course> findStudentCourses(String studentId) {
		ClientSession clientSession = mongoClient.startSession();
		List<String> courseIds = studentCollection.find(clientSession, Filters.eq(ID, studentId))
				.first().getList(COURSES, String.class);
		List<Course> returnedCourses = new ArrayList<>();
		for (String course : courseIds) {
			returnedCourses.add(fromDocumentToCourse(courseCollection
					.find(clientSession, Filters.eq(ID, course)).first()));
		}

		return returnedCourses;
	}

	private Student fromDocumentToStudent(Document document) {
		return new Student(document.getString(ID), document.getString("name"));
	}
	
	private Course fromDocumentToCourse(Document document) {
		return new Course(document.getString(ID), document.getString("name"), document.getString("cfu"));
	}

	public ClientSession getClientSession() {
		return clientSession;
	}
}
