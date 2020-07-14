package service;

import java.util.List;

import model.Course;
import model.Student;
import repository.StudentRepository;
import repository.TransactionManager;

public class AgendaService {

	private TransactionManager transactionManager;

	public AgendaService(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public List<Student> getAllStudents() {
		return transactionManager.studentTransaction(StudentRepository::findAll);
	}

	public Boolean findStudent(Student student) {
		return transactionManager.studentTransaction(studentRepository -> {
			return studentRepository.findById(student.getId()) != null;
		});
	}
	
	public Boolean findCourse(Course course) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addStudent(Student student) {
		// TODO Auto-generated method stub
		
	}

	public void removeStudent(Student student) {
		// TODO Auto-generated method stub
		
	}

	public void addCourseToStudent(Student student, Course course) {
		// TODO Auto-generated method stub
		
	}

	public void removeCourseFromStudent(Student student, Course course) {
		// TODO Auto-generated method stub
		
	}

	public Boolean studentHasCourse(Student student, Course course) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addCourse(Course course) {
		// TODO Auto-generated method stub
		
	}

	public void removeCourse(Course course) {
		// TODO Auto-generated method stub
		
	}

	public Boolean courseHasStudent(Student student, Course course) {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeStudentFromCourse(Student student, Course course) {
		// TODO Auto-generated method stub
		
	}

	public void addStudentToCourse(Student student, Course course) {
		// TODO Auto-generated method stub
		
	}

	public List<Course> getAllCourses() {
		// TODO Auto-generated method stub
		return null;
	}

}
