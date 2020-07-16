package service;

import java.util.List;

import model.Course;
import model.Student;
import repository.CourseRepository;
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
		return transactionManager.courseTransaction(courseRepository -> {
			return courseRepository.findById(course.getId()) != null;
		});
	}

	public void addStudent(Student student) {
		transactionManager.studentTransaction(studentRepository -> {
			if (student != null)
				studentRepository.save(student);
			return null;
		});
	}

	public void removeStudent(Student student) {
		transactionManager.studentTransaction(studentRepository -> {
			if (student != null)
				studentRepository.delete(student);
			return null;
		});		
	}

	public void addCourseToStudent(Student student, Course course) {
		transactionManager.studentTransaction(studentRepository -> {
			if (studentRepository.findById(student.getId()) != null)
				studentRepository.updateStudentCourses(student.getId(), course.getId());
			return null;
		});
	}

	public void removeCourseFromStudent(Student student, Course course) {
		// TODO Auto-generated method stub
		
	}

	public Boolean studentHasCourse(Student student, Course course) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addCourse(Course course) {
		transactionManager.courseTransaction(courseRepository -> {
			if (course != null)
				courseRepository.save(course);
			return null;
		});		
	}

	public void removeCourse(Course course) {
		transactionManager.courseTransaction(courseRepository -> {
			if (course != null)
				courseRepository.delete(course);
			return null;
		});
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
		return transactionManager.courseTransaction(CourseRepository::findAll);
	}

}
