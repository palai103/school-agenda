package repository;

import java.util.List;

import com.mongodb.client.ClientSession;

import model.Course;
import model.Student;

public interface CourseRepository {
	
	List<Course> findAll(ClientSession clientSession);
	
	Course findById(String id, ClientSession clientSession);

	void save(Course course, ClientSession clientSession);

	void delete(Course course, ClientSession clientSession);

	void updateCourseStudents(String studentId, String courseId, ClientSession clientSession);

	void removeCourseStudent(String studentId, String courseId, ClientSession clientSession);

	List<Student> findCourseStudents(String courseId, ClientSession clientSession);

}
