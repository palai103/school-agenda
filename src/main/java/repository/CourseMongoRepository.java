package repository;

import java.util.Collections;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import model.Course;

public class CourseMongoRepository implements CourseRepository{
	
	private MongoCollection<Document> courseCollection;

	public CourseMongoRepository(MongoClient mongoClient, String dbName, String dbCollection) {
		courseCollection = mongoClient.getDatabase(dbName).getCollection(dbCollection);
	}

	@Override
	public List<Course> findAll() {
		return Collections.emptyList();
	}

	@Override
	public Course findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Course course) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Course course) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateCourseStudents(String studentId, String courseId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeCourseStudent(String studentId, String courseId) {
		// TODO Auto-generated method stub
		
	}

}
