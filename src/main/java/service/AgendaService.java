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
		return transactionManager
				.studentTransaction((studentRepository, clientSession) -> 
				studentRepository.findAll(clientSession));
	}

	public Boolean findStudent(Student student) {
		return transactionManager
				.studentTransaction((studentRepository, clientSession) -> 
				studentRepository.findById(clientSession, student.getId()) != null);
	}

	public Boolean findCourse(Course course) {
		return transactionManager
				.courseTransaction((courseRepository, clientSession) -> 
				courseRepository.findById(clientSession, course.getId()) != null);
	}

	public void addStudent(Student student) {
		transactionManager.studentTransaction((studentRepository, clientSession) -> {
			if (student != null)
				studentRepository.save(clientSession, student);
			return null;
		});
	}

	public void removeStudent(Student student) {
		transactionManager.compositeTransaction((studentRepository, courseRepository, clientSession) -> {
			if (student != null) {
				List<String> studentCourses = studentRepository.findStudentCourses(clientSession, student.getId());
				for (String courseId : studentCourses) {
					courseRepository.removeCourseStudent(clientSession, student.getId(), courseId);
				}
				studentRepository.delete(clientSession, student);
			}
			return null;
		});
	}

	public void addCourseToStudent(Student student, Course course) {
		transactionManager.compositeTransaction((studentRepository, courseRepository, clientSession) -> {
			if (studentRepository.findById(clientSession, student.getId()) != null) {
				studentRepository.updateStudentCourses(clientSession, student.getId(), course.getId());
				courseRepository.updateCourseStudents(clientSession, student.getId(), course.getId());
			}
			return null;
		});
	}

	public void removeCourseFromStudent(Student student, Course course) {
		transactionManager.compositeTransaction((studentRepository, courseRepository, clientSession) -> {
			if (studentRepository.findById(clientSession, student.getId()) != null) {
				studentRepository.removeStudentCourse(clientSession, student.getId(), course.getId());
				courseRepository.removeCourseStudent(clientSession, student.getId(), course.getId());
			}
			return null;
		});
	}

	public Boolean studentHasCourse(Student student, Course course) {
		List<String> studentCourses = transactionManager
				.studentTransaction((studentRepository, clientSession) -> 
				studentRepository.findStudentCourses(clientSession, student.getId()));
		return studentCourses.contains(course.getId());
	}

	public void addCourse(Course course) {
		transactionManager.courseTransaction((courseRepository, clientSession) -> {
			if (course != null)
				courseRepository.save(clientSession, course);
			return null;
		});
	}

	public void removeCourse(Course course) {
		transactionManager.compositeTransaction((studentRepository, courseRepository, clientSession) -> {
			if (course != null) {
				List<String> courseStudents = courseRepository.findCourseStudents(clientSession, course.getId());
				for (String studentId : courseStudents) {
					studentRepository.removeStudentCourse(clientSession, studentId, course.getId());
				}
				courseRepository.delete(clientSession, course);
			}
			return null;
		});
	}

	public Boolean courseHasStudent(Student student, Course course) {
		List<String> courseStudents = transactionManager
				.courseTransaction((courseRepository, clientSession) -> 
				courseRepository.findCourseStudents(clientSession, course.getId()));
		return courseStudents.contains(student.getId());
	}

	public void removeStudentFromCourse(Student student, Course course) {
		transactionManager.compositeTransaction((studentRepository, courseRepository, clientSession) -> {
			if (courseRepository.findById(clientSession, course.getId()) != null) {
				courseRepository.removeCourseStudent(clientSession, student.getId(), course.getId());
				studentRepository.removeStudentCourse(clientSession, student.getId(), course.getId());
			}
			return null;
		});
	}

	public void addStudentToCourse(Student student, Course course) {
		transactionManager.compositeTransaction((studentRepository, courseRepository, clientSession) -> {
			if (courseRepository.findById(clientSession, course.getId()) != null) {
				courseRepository.updateCourseStudents(clientSession, student.getId(), course.getId());
				studentRepository.updateStudentCourses(clientSession, student.getId(), course.getId());
			}
			return null;
		});
	}

	public List<Course> getAllCourses() {
		return transactionManager.courseTransaction(CourseRepository::findAll);
	}

}
