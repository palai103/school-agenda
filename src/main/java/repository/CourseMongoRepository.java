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

import model.Course;

public class CourseMongoRepository implements CourseRepository{
	
	private static final String ID = "id";
	private static final String STUDENTS = "students";
	private MongoCollection<Document> courseCollection;

	public CourseMongoRepository(MongoClient mongoClient, String dbName, String dbCollection) {
		courseCollection = mongoClient.getDatabase(dbName).getCollection(dbCollection);
	}

	@Override
	public List<Course> findAll() {
		return StreamSupport.
				stream(courseCollection.find().spliterator(), false)
				.map(this::fromDocumentToCourse)
				.collect(Collectors.toList());
	}

	@Override
	public Course findById(String id) {
		Document document  = courseCollection.find(Filters.eq(ID, id)).first();
		if(document != null) {
			return fromDocumentToCourse(document);
		}
		return null;
	}

	@Override
	public void save(Course course) {
		courseCollection.insertOne(
				new Document()
				.append(ID, course.getId())
				.append("name", course.getName())
				.append(STUDENTS, Collections.emptyList()));
	}

	@Override
	public void delete(Course course) {
		courseCollection.deleteOne(Filters.eq(ID, course.getId()));		
	}

	@Override
	public void updateCourseStudents(String studentId, String courseId) {
		courseCollection.updateOne(Filters.eq(ID, courseId), 
				Updates.push(STUDENTS, studentId));		
	}

	@Override
	public void removeCourseStudent(String studentId, String courseId) {
		courseCollection.updateOne(Filters.eq(ID, courseId), 
				Updates.pull(STUDENTS, studentId));		
	}
	
	private Course fromDocumentToCourse(Document document) {
		return new Course(document.getString(ID), document.getString("name"));
	}

	@Override
	public List<String> findCourseStudents(String courseId) {
		return courseCollection.find(Filters.eq(ID, courseId))
				.first().getList(STUDENTS, String.class);
	}

}
