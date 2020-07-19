package repository;

import java.util.List;

import model.Course;

public interface CourseRepository {

	List<Course> findAll();
	
	Course findById(String id);

	void save(Course course);

	void delete(Course course);

	void updateCourseStudents(String studentId, String courseId);

	void removeCourseStudent(String studentId, String courseId);

	List<String> findCourseStudents(String courseId);

}
