package repository;

import java.util.List;

import com.mongodb.client.ClientSession;

import model.Course;
import model.Student;

public interface StudentRepository {

	List<Student> findAll(ClientSession clientSession);

	Student findById(String id, ClientSession clientSession);

	void save(Student student, ClientSession clientSession);

	void delete(Student student, ClientSession clientSession);

	void updateStudentCourses(String studenId, String courseId, ClientSession clientSession);

	void removeStudentCourse(String studentId, String courseId, ClientSession clientSession);

	List<Course> findStudentCourses(String studentId, ClientSession clientSession);

}
