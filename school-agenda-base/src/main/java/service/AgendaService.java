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
		return transactionManager.studentTransaction(
				studentRepository -> studentRepository.findById(student.getId()) != null);
	}

	public Boolean findCourse(Course course) {
		return transactionManager.courseTransaction(
				courseRepository -> courseRepository.findById(course.getId()) != null);
	}

	public void addStudent(Student student) {
		transactionManager.studentTransaction(studentRepository -> {
			if (student != null)
				studentRepository.save(student);
			return null;
		});
	}

	public void removeStudent(Student student) {
		transactionManager.compositeTransaction((studentRepository, courseRepository) -> {
			if (student != null) {
				List<Course> studentCourses = studentRepository.findStudentCourses(student.getId());
				for (Course course : studentCourses) {
					courseRepository.removeCourseStudent(student.getId(), course.getId());
				}
				studentRepository.delete(student);
			}
			return null;
		});
	}

	public void addCourseToStudent(Student student, Course course) {
		transactionManager.compositeTransaction((studentRepository, courseRepository) -> {
			if (studentRepository.findById(student.getId()) != null) {
				studentRepository.updateStudentCourses(student.getId(), course.getId());
				courseRepository.updateCourseStudents(student.getId(), course.getId());
			}
			return null;
		});
	}

	public void removeCourseFromStudent(Student student, Course course) {
		transactionManager.compositeTransaction((studentRepository, courseRepository) -> {
			if (studentRepository.findById(student.getId()) != null) {
				studentRepository.removeStudentCourse(student.getId(), course.getId());
				courseRepository.removeCourseStudent(student.getId(), course.getId());
			}
			return null;
		});
	}

	public Boolean studentHasCourse(Student student, Course course) {
		List<Course> studentCourses = transactionManager.studentTransaction(
				studentRepository -> studentRepository.findStudentCourses(student.getId()));
		return studentCourses.contains(course);
	}

	public void addCourse(Course course) {
		transactionManager.courseTransaction(courseRepository -> {
			if (course != null)
				courseRepository.save(course);
			return null;
		});
	}

	public void removeCourse(Course course) {
		transactionManager.compositeTransaction((studentRepository, courseRepository) -> {
			if (course != null) {
				List<Student> courseStudents = courseRepository.findCourseStudents(course.getId());
				for (Student student : courseStudents) {
					studentRepository.removeStudentCourse(student.getId(), course.getId());
				}
				courseRepository.delete(course);
			}
			return null;
		});
	}

	public Boolean courseHasStudent(Student student, Course course) {
		List<Student> courseStudents = transactionManager.courseTransaction(
				courseRepository -> courseRepository.findCourseStudents(course.getId()));
		return courseStudents.contains(student);
	}

	public void removeStudentFromCourse(Student student, Course course) {
		transactionManager.compositeTransaction((studentRepository, courseRepository) -> {
			if (courseRepository.findById(course.getId()) != null) {
				courseRepository.removeCourseStudent(student.getId(), course.getId());
				studentRepository.removeStudentCourse(student.getId(), course.getId());
			}
			return null;
		});
	}

	public void addStudentToCourse(Student student, Course course) {
		transactionManager.compositeTransaction((studentRepository, courseRepository) -> {
			if (courseRepository.findById(course.getId()) != null) {
				courseRepository.updateCourseStudents(student.getId(), course.getId());
				studentRepository.updateStudentCourses(student.getId(), course.getId());
			}
			return null;
		});
	}

	public List<Course> getAllCourses() {
		return transactionManager.courseTransaction(CourseRepository::findAll);
	}

	public List<Course> getAllStudentCourses(Student student) {
		return transactionManager.studentTransaction(
				studentRepository -> studentRepository.findStudentCourses(student.getId()));
	}

	public List<Student> getAllCourseStudents(Course course) {
		return transactionManager.courseTransaction(
				courseRepository -> courseRepository.findCourseStudents(course.getId()));
	}

}
