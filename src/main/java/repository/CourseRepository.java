package repository;

import java.util.List;

import com.mongodb.client.ClientSession;

import model.Course;

public interface CourseRepository {
	
	List<Course> findAll(ClientSession clientSession);
	
	Course findById(ClientSession clientSession, String id);

	void save(ClientSession clientSession, Course course);

	void delete(ClientSession clientSession, Course course);

	void updateCourseStudents(ClientSession clientSession, String studentId, String courseId);

	void removeCourseStudent(ClientSession clientSession, String studentId, String courseId);

	List<String> findCourseStudents(ClientSession clientSession, String courseId);

}
