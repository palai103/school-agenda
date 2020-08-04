package view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import controller.AgendaController;
import model.Course;
import model.Student;
import picocli.CommandLine.Command;

@Command
public class AgendaViewCli implements AgendaView {

	private static final String STUDENT_WITH_ID = "Student with id ";
	private static final String INSERT_COURSE_ID = "Insert course id: ";
	private static final String COURSE_WITH_ID = "Course with id ";
	private static final String INSERT_STUDENT_ID = "Insert student id: ";
	private AgendaController controller;
	private InputStream inputStream;
	private PrintStream printStream;
	private Scanner scanner;

	public AgendaViewCli(InputStream inputStream, PrintStream printStream) {
		this.inputStream = inputStream;
		this.printStream = printStream;
	}

	@Override
	public void showAllStudents(List<Student> allStudents) {
		for (Student student : allStudents) {
			printStream.println(student.toString());
		}
	}

	@Override
	public void notifyStudentAdded(Student student) {
		printStream.println("Added " + student.toString());
	}

	@Override
	public void notifyStudentNotAdded(Student student) {
		printStream.println(student.toString() + " not added");
	}

	@Override
	public void notifyStudentRemoved(Student student) {
		printStream.println("Removed " + student.toString());
	}

	@Override
	public void notifyStudentNotRemoved(Student student) {
		printStream.println(STUDENT_WITH_ID + student.getId() + " not removed");
	}

	@Override
	public void notifyCourseAddedToStudent(Student student, Course course) {
		printStream.println(COURSE_WITH_ID + course.getId() + " added to student with id " + student.getId());
	}

	@Override
	public void notifyCourseNotAddedToStudent(Student student, Course course) {
		printStream.println(COURSE_WITH_ID + course.getId() + " not added to student with id " + student.getId());
	}

	@Override
	public void notifyCourseRemovedFromStudent(Student student, Course course) {
		printStream.println(COURSE_WITH_ID + course.getId() + " removed from student with id " + student.getId());
	}

	@Override
	public void notifyCourseNotRemovedFromStudent(Student student, Course course) {
		printStream.println(COURSE_WITH_ID + course.getId() + " not removed from student with id " + student.getId());
	}

	@Override
	public void notifyCourseAdded(Course course) {
		printStream.println("Added " + course.toString());
	}

	@Override
	public void notifyCourseNotAdded(Course course) {
		printStream.println(course.toString() + " not added");
	}

	@Override
	public void notifyCourseRemoved(Course course) {
		printStream.println("Removed " + course.toString());
	}

	@Override
	public void notifyCourseNotRemoved(Course course) {
		printStream.println(course.toString() + " not removed");

	}

	@Override
	public void notifyStudentRemovedFromCourse(Student student, Course course) {
		printStream.println(STUDENT_WITH_ID + student.getId() + " removed from course with id " + course.getId());
	}

	@Override
	public void notifyStudentNotRemovedFromCourse(Student student, Course course) {
		printStream.println(STUDENT_WITH_ID + student.getId() + " not removed from course with id " + course.getId());
	}

	@Override
	public void notifyStudentNotAddedToCourse(Student student, Course course) {
		printStream.println(STUDENT_WITH_ID + student.getId() + " not added to course with id " + course.getId());
	}

	@Override
	public void notifyStudentAddedToCourse(Student student, Course course) {
		printStream.println(STUDENT_WITH_ID + student.getId() + " added to course with id " + course.getId());
	}

	@Override
	public void showAllCourses(List<Course> allCourses) {
		for (Course course : allCourses) {
			printStream.println(course.toString());
		}
	}

	public void inject(AgendaController controller) {
		this.controller = controller;

	}

	public void showMenu() {
		printStream.println("--------- Pick a choice: ---------\r\n" + "1) Show all students\r\n"
				+ "2) Show all courses\r\n" + "3) Add a student\r\n" + "4) Add a course\r\n"
				+ "5) Enroll a student to a course (by student)\r\n" + "6) Enroll a student to a course (by course)\r\n"
				+ "7) Delete a student enrollment (by student id)\r\n"
				+ "8) Delete a student enrollment (by course id)\r\n" + "9) Delete a student\r\n"
				+ "10) Delete a course\r\n" + "11) Exit\r\n" + "---------------------------------");
	}

	public void setInput(ByteArrayInputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void menuChoice() {
		showMenu();

		scanner = new Scanner(inputStream);
		String choice = scanner.nextLine();

		switch (choice) {
		case "1":
			showAllStudentsCallController();
			break;
		case "2":
			showAllCoursesCallController();
			break;
		case "3":
			addStudentCallController();
			break;
		case "4":
			addCourseCallController();
			break;
		case "5":
			addCourseToStudentCallController();
			break;
		case "6":
			addStudentToCourseCallController();
			break;
		case "7":
			removeCourseToStudentCallController();
			break;
		case "8":
			removeStudentToCourseCallController();
			break;
		case "9":
			removeStudentCallController();
			break;
		case "10":
			removeCourseCallControler();
			break;
		case "11":
			System.exit(0);
			break;
		default:
			break;
		}

	}

	private void removeStudentToCourseCallController() {
		printStream.print(INSERT_STUDENT_ID);
		String studentId = scanner.nextLine();
		printStream.print(INSERT_COURSE_ID);
		String courseId = scanner.nextLine();
		controller.removeStudentFromCourse(new Student(studentId, ""), new Course(courseId, ""));
	}

	private void addStudentToCourseCallController() {
		printStream.print(INSERT_STUDENT_ID);
		String studentId = scanner.nextLine();
		printStream.print(INSERT_COURSE_ID);
		String courseId = scanner.nextLine();
		controller.addStudentToCourse(new Student(studentId, ""), new Course(courseId, ""));
	}

	private void removeCourseCallControler() {
		printStream.print(INSERT_COURSE_ID);
		String id = scanner.nextLine();
		controller.removeCourse(new Course(id, ""));
	}

	private void addCourseCallController() {
		printStream.print(INSERT_COURSE_ID);
		String id = scanner.nextLine();
		printStream.print("Insert course name: ");
		String name = scanner.nextLine();
		controller.addCourse(new Course(id, name));
	}

	private void removeCourseToStudentCallController() {
		printStream.print(INSERT_STUDENT_ID);
		String studentId = scanner.nextLine();
		printStream.print(INSERT_COURSE_ID);
		String courseId = scanner.nextLine();
		controller.removeCourseFromStudent(new Student(studentId, ""), new Course(courseId, ""));
	}

	private void addCourseToStudentCallController() {
		printStream.print(INSERT_STUDENT_ID);
		String studentId = scanner.nextLine();
		printStream.print(INSERT_COURSE_ID);
		String courseId = scanner.nextLine();
		controller.addCourseToStudent(new Student(studentId, ""), new Course(courseId, ""));
	}

	private void removeStudentCallController() {
		printStream.print(INSERT_STUDENT_ID);
		String id = scanner.nextLine();
		controller.removeStudent(new Student(id, ""));
	}

	private void addStudentCallController() {
		printStream.print(INSERT_STUDENT_ID);
		String id = scanner.nextLine();
		printStream.print("Insert student name: ");
		String name = scanner.nextLine();
		controller.addStudent(new Student(id, name));
	}

	private void showAllStudentsCallController() {
		controller.getAllStudents();
	}

	private void showAllCoursesCallController() {
		controller.getAllCourses();
	}

}
