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

import model.Course;

public class CourseMongoRepository implements CourseRepository{
	
	private static final String ID = "id";
	private static final String STUDENTS = "students";
	private MongoCollection<Document> courseCollection;

	public CourseMongoRepository(MongoClient mongoClient, String dbName, String dbCollection) {
		courseCollection = mongoClient.getDatabase(dbName).getCollection(dbCollection);
	}

	@Override
	public List<Course> findAll(ClientSession clientSession) {
		return StreamSupport.
				stream(courseCollection.find(clientSession).spliterator(), false)
				.map(this::fromDocumentToCourse)
				.collect(Collectors.toList());
	}

	@Override
	public Course findById(ClientSession clientSession, String id) {
		Document document  = courseCollection.find(clientSession, Filters.eq(ID, id)).first();
		if(document != null) {
			return fromDocumentToCourse(document);
		}
		return null;
	}

	@Override
	public void save(ClientSession clientSession, Course course) {
		courseCollection.insertOne(clientSession,
				new Document()
				.append(ID, course.getId())
				.append("name", course.getName())
				.append("cfu", course.getCFU())
				.append(STUDENTS, Collections.emptyList()));
	}

	@Override
	public void delete(ClientSession clientSession, Course course) {
		courseCollection.deleteOne(clientSession, Filters.eq(ID, course.getId()));		
	}

	@Override
	public void updateCourseStudents(ClientSession clientSession, String studentId, String courseId) {
		courseCollection.updateOne(clientSession, Filters.eq(ID, courseId), 
				Updates.push(STUDENTS, studentId));		
	}

	@Override
	public void removeCourseStudent(ClientSession clientSession, String studentId, String courseId) {
		courseCollection.updateOne(clientSession, Filters.eq(ID, courseId), 
				Updates.pull(STUDENTS, studentId));		
	}
	
	@Override
	public List<String> findCourseStudents(ClientSession clientSession, String courseId) {
		return courseCollection.find(clientSession, Filters.eq(ID, courseId))
				.first().getList(STUDENTS, String.class);
	}
	
	private Course fromDocumentToCourse(Document document) {
		return new Course(document.getString(ID), document.getString("name"), document.getString("cfu"));
	}


}
