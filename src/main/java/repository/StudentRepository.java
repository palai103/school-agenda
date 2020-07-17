package repository;

import java.util.List;

import model.Course;
import model.Student;

public interface StudentRepository {

	List<Student> findAll();

	Student findById(String id);

	void save(Student student);

	void delete(Student student);

	void updateStudentCourses(String studenId, String courseId);

	void removeStudentCourse(String studentId, String courseId);

	List<String> findStudentCourses(String studentId);

}
