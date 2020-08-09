package repository;

import java.util.List;

import com.mongodb.client.ClientSession;

import model.Course;
import model.Student;

public interface StudentRepository {

	List<Student> findAll(ClientSession clientSession);

	Student findById(ClientSession clientSession, String id);

	void save(ClientSession clientSession, Student student);

	void delete(ClientSession clientSession, Student student);

	void updateStudentCourses(ClientSession clientSession, String studenId, String courseId);

	void removeStudentCourse(ClientSession clientSession, String studentId, String courseId);

	List<Course> findStudentCourses(ClientSession clientSession, String studentId);

}
