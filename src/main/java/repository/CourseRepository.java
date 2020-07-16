package repository;

import java.util.List;

import model.Course;

public interface CourseRepository {

	List<Course> findAll();
	
	Course findById(String id);

	void save(Course course);

	void delete(Course course);

}
