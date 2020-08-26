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

public class CourseMongoRepository implements CourseRepository{
	
	private static final String ID = "id";
	private static final String STUDENTS = "students";
	private MongoCollection<Document> courseCollection;
	private MongoCollection<Document> studentCollection;

	public CourseMongoRepository(MongoClient mongoClient, String dbName, String dbCollection, String studentCollectionName) {
		this.mongoClient = mongoClient;
		courseCollection = mongoClient.getDatabase(dbName).getCollection(dbCollection);
		studentCollection = mongoClient.getDatabase(dbName).getCollection(studentCollectionName);
	}

	@Override
	public List<Course> findAll(ClientSession clientSession) {
		return StreamSupport.
				stream(courseCollection.find(clientSession).spliterator(), false)
				.map(this::fromDocumentToCourse)
				.collect(Collectors.toList());
	}

	@Override
	public Course findById(String id, ClientSession clientSession) {
		Document document  = courseCollection.find(clientSession, Filters.eq(ID, id)).first();
		
		if(document != null) {
			return fromDocumentToCourse(document);
		}
		return null;
	}

	@Override
	public void save(Course course, ClientSession clientSession) {
		courseCollection.insertOne(clientSession,
				new Document()
				.append(ID, course.getId())
				.append("name", course.getName())
				.append("cfu", course.getCFU())
				.append(STUDENTS, Collections.emptyList()));
		
	}

	@Override
	public void delete(Course course, ClientSession clientSession) {
		courseCollection.deleteOne(clientSession, Filters.eq(ID, course.getId()));	
		
	}

	@Override
	public void updateCourseStudents(String studentId, String courseId, ClientSession clientSession) {
		courseCollection.updateOne(clientSession, Filters.eq(ID, courseId), 
				Updates.push(STUDENTS, studentId));
		
	}

	@Override
	public void removeCourseStudent(String studentId, String courseId, ClientSession clientSession) {
		courseCollection.updateOne(clientSession, Filters.eq(ID, courseId), 
				Updates.pull(STUDENTS, studentId));
		
	}
	
	@Override
	public List<Student> findCourseStudents(String courseId, ClientSession clientSession) {
		List<String> studentIds = courseCollection.find(clientSession, Filters.eq(ID, courseId))
				.first().getList(STUDENTS, String.class);
		List<Student> returnedStudents = new ArrayList<>();
		for (String student : studentIds) {
			returnedStudents.add(fromDocumentToStudent(studentCollection
					.find(clientSession, Filters.eq(ID, student)).first()));
		}
		
		return returnedStudents;
	}
	
	private Course fromDocumentToCourse(Document document) {
		return new Course(document.getString(ID), document.getString("name"), document.getString("cfu"));
	}

	private Student fromDocumentToStudent(Document document) {
		return new Student(document.getString(ID), document.getString("name"));
	}
}
