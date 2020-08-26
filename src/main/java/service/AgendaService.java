package service;

import java.util.List;

import model.Course;
import model.Student;
import repository.TransactionManager;

public class AgendaService {

	private TransactionManager transactionManager;

	public AgendaService(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public List<Student> getAllStudents() {
		return transactionManager.studentTransaction((clientSession, studentRepository) -> studentRepository.findAll(clientSession));
	}

	public Boolean findStudent(Student student) {
		return transactionManager.studentTransaction(
				(clientSession, studentRepository) -> studentRepository.findById(student.getId(), clientSession) != null);
	}

	public Boolean findCourse(Course course) {
		return transactionManager.courseTransaction(
				(clientSession, courseRepository) -> courseRepository.findById(course.getId(), clientSession) != null);
	}

	public void addStudent(Student student) {
		transactionManager.studentTransaction((clientSession, studentRepository) -> {
			if (student != null)
				studentRepository.save(student, clientSession);
			return null;
		});
	}

	public void removeStudent(Student student) {
		transactionManager.compositeTransaction((clientSession, studentRepository, courseRepository) -> {
			if (student != null) {
				List<Course> studentCourses = studentRepository.findStudentCourses(student.getId(), clientSession);
				for (Course course : studentCourses) {
					courseRepository.removeCourseStudent(student.getId(), course.getId(), clientSession);
				}
				studentRepository.delete(student, clientSession);
			}
			return null;
		});
	}

	public void addCourseToStudent(Student student, Course course) {
		transactionManager.compositeTransaction((clientSession, studentRepository, courseRepository) -> {
			if (studentRepository.findById(student.getId(), clientSession) != null) {
				studentRepository.updateStudentCourses(student.getId(), course.getId(), clientSession);
				courseRepository.updateCourseStudents(student.getId(), course.getId(), clientSession);
			}
			return null;
		});
	}

	public void removeCourseFromStudent(Student student, Course course) {
		transactionManager.compositeTransaction((clientSession, studentRepository, courseRepository) -> {
			if (studentRepository.findById(student.getId(), clientSession) != null) {
				studentRepository.removeStudentCourse(student.getId(), course.getId(), clientSession);
				courseRepository.removeCourseStudent(student.getId(), course.getId(), clientSession);
			}
			return null;
		});
	}

	public Boolean studentHasCourse(Student student, Course course) {
		List<Course> studentCourses = transactionManager.studentTransaction(
				(clientSession, studentRepository) -> studentRepository.findStudentCourses(student.getId(), clientSession));
		return studentCourses.contains(course);
	}

	public void addCourse(Course course) {
		transactionManager.courseTransaction((clientSession, courseRepository) -> {
			if (course != null)
				courseRepository.save(course, clientSession);
			return null;
		});
	}

	public void removeCourse(Course course) {
		transactionManager.compositeTransaction((clientSession, studentRepository, courseRepository) -> {
			if (course != null) {
				List<Student> courseStudents = courseRepository.findCourseStudents(course.getId(), clientSession);
				for (Student student : courseStudents) {
					studentRepository.removeStudentCourse(student.getId(), course.getId(), clientSession);
				}
				courseRepository.delete(course, clientSession);
			}
			return null;
		});
	}

	public Boolean courseHasStudent(Student student, Course course) {
		List<Student> courseStudents = transactionManager.courseTransaction(
				(clientSession, courseRepository) -> courseRepository.findCourseStudents(course.getId(), clientSession));
		return courseStudents.contains(student);
	}

	public void removeStudentFromCourse(Student student, Course course) {
		transactionManager.compositeTransaction((clientSession, studentRepository, courseRepository) -> {
			if (courseRepository.findById(course.getId(), clientSession) != null) {
				courseRepository.removeCourseStudent(student.getId(), course.getId(), clientSession);
				studentRepository.removeStudentCourse(student.getId(), course.getId(), clientSession);
			}
			return null;
		});
	}

	public void addStudentToCourse(Student student, Course course) {
		transactionManager.compositeTransaction((clientSession, studentRepository, courseRepository) -> {
			if (courseRepository.findById(course.getId(), clientSession) != null) {
				courseRepository.updateCourseStudents(student.getId(), course.getId(), clientSession);
				studentRepository.updateStudentCourses(student.getId(), course.getId(), clientSession);
			}
			return null;
		});
	}

	public List<Course> getAllCourses() {
		return transactionManager.courseTransaction((clientSession, courseRepository) -> courseRepository.findAll(clientSession));
	}

	public List<Course> getAllStudentCourses(Student student) {
		return transactionManager.studentTransaction(
				(clientSession, studentRepository) -> studentRepository.findStudentCourses(student.getId(), clientSession));
	}

	public List<Student> getAllCourseStudents(Course course) {
		return transactionManager.courseTransaction(
				(clientSession, courseRepository) -> courseRepository.findCourseStudents(course.getId(), clientSession));
	}

}
