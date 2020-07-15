package repository;

import model.Course;

public interface CourseRepository {

	Course findById(String id);

}
