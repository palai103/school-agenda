package view;

import java.util.List;

import model.Course;
import model.Student;

public interface AgendaView {

	void showAllStudents(List<Student> allStudents);

	void notifyStudentAdded(Student student);

	void notifyStudentNotAdded(Student student);

	void notifyStudentRemoved(Student student);

	void notifyStudentNotRemoved(Student student);

	void notifyCourseAddedToStudent(Student student, Course course);

	void notifyCourseNotAddedToStudent(Student student, Course course);

	void notifyCourseRemovedFromStudent(Student student, Course course);

	void notifyCourseNotRemovedFromStudent(Student student, Course course);

	void notifyCourseAdded(Course course);

	void notifyCourseNotAdded(Course course);

	void notifyCourseRemoved(Course course);

	void notifyCourseNotRemoved(Course course);

	void notifyStudentRemovedFromCourse(Student student, Course course);

	void notifyStudentAddedToCourse(Student student, Course course);

}
