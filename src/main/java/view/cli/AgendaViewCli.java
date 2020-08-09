package view.cli;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import controller.AgendaController;
import model.Course;
import model.Student;
import view.AgendaView;

public class AgendaViewCli implements AgendaView {

	private static final String STUDENT_WITH_ID = "Student with id ";
	private static final String INSERT_COURSE_ID = "Insert course id: ";
	private static final String COURSE_WITH_ID = "Course with id ";
	private static final String INSERT_STUDENT_ID = "Insert student id: ";
	private static final String NEWLINE = System.getProperty("line.separator");
	private AgendaController controller;
	private InputStream inputStream;
	private PrintStream printStream;
	private Scanner scanner;
	private ArrayList<Student> students;
	private ArrayList<Course> courses;

	public AgendaViewCli(InputStream inputStream, PrintStream printStream) {
		this.inputStream = inputStream;
		this.printStream = printStream;
		students = new ArrayList<>();
		courses = new ArrayList<>();
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
		printStream.println(STUDENT_WITH_ID + student.getId() + " removed");
		students.remove(student);
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
		printStream.println(COURSE_WITH_ID + course.getId() + " removed");
		courses.remove(course);
	}

	@Override
	public void notifyCourseNotRemoved(Course course) {
		printStream.println(COURSE_WITH_ID + course.getId() + " not removed");

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

	@Override
	public void showAllStudentCourses(List<Course> studentCourses) {
		for (Course course : studentCourses) {
			printStream.println(course.toString());
		}
	}

	@Override
	public void showAllCourseStudents(List<Student> courseStudents) {
		for (Student student : courseStudents) {
			printStream.println(student.toString());
		}
	}

	public void inject(AgendaController controller) {
		this.controller = controller;

	}

	public void showMenu() {
		printStream.println("--------- Pick a choice: ---------" + NEWLINE + "1) Show all students" + NEWLINE
				+ "2) Show all courses" + NEWLINE + "3) Add a student" + NEWLINE + "4) Add a course" + NEWLINE
				+ "5) Enroll a student to a course (by student)" + NEWLINE
				+ "6) Enroll a student to a course (by course)" + NEWLINE
				+ "7) Delete a student enrollment (by student id)" + NEWLINE
				+ "8) Delete a student enrollment (by course id)" + NEWLINE + "9) Delete a student" + NEWLINE
				+ "10) Delete a course" + NEWLINE + "11) Show all student courses" + NEWLINE
				+ "12) Show all course students" + NEWLINE + "13) Exit" + NEWLINE
				+ "---------------------------------");
	}

	public void setInput(ByteArrayInputStream inputStream) {
		this.inputStream = inputStream;
	}

	public int menuChoice() {
		showMenu();

		scanner = new Scanner(inputStream);
		String choice = scanner.nextLine();
		int code = 0;

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
			showAllStudentCoursesCallController();
			break;
		case "12":
			showAllCourseStudentsCallController();
			break;
		case "13":
			code = -1;
			break;
		default:
			break;
		}
		return code;
	}

	private void showAllCourseStudentsCallController() {
		printStream.print(INSERT_COURSE_ID);
		String courseId = scanner.nextLine();
		controller.getAllCourseStudents(new Course(courseId, "", ""));
	}

	private void showAllStudentCoursesCallController() {
		printStream.print(INSERT_STUDENT_ID);
		String studentId = scanner.nextLine();
		controller.getAllStudentCourses(new Student(studentId, ""));
	}

	private void removeStudentToCourseCallController() {
		printStream.print(INSERT_STUDENT_ID);
		String studentId = scanner.nextLine();
		printStream.print(INSERT_COURSE_ID);
		String courseId = scanner.nextLine();
		Student studentToFind;
		try {
			studentToFind = students.stream().filter(
					student -> student.getId().equals(studentId)).collect(Collectors.toList()).get(0);
		} catch (Exception e) {
			studentToFind = new Student(studentId, "");
		}
		controller.removeStudentFromCourse(studentToFind, new Course(courseId, "", ""));
	}

	private void addStudentToCourseCallController() {
		printStream.print(INSERT_STUDENT_ID);
		String studentId = scanner.nextLine();
		printStream.print(INSERT_COURSE_ID);
		String courseId = scanner.nextLine();
		controller.addStudentToCourse(new Student(studentId, ""), new Course(courseId, "", ""));
	}

	private void removeCourseCallControler() {
		printStream.print(INSERT_COURSE_ID);
		String id = scanner.nextLine();
		Course courseToFind;
		try {
			courseToFind = courses.stream().filter(
					course -> course.getId().equals(id)).collect(Collectors.toList()).get(0);
		} catch (Exception e) {
			courseToFind = new Course(id, "", "");
		}
		controller.removeCourse(courseToFind);
	}

	private void addCourseCallController() {
		printStream.print(INSERT_COURSE_ID);
		String id = scanner.nextLine();
		printStream.print("Insert course name: ");
		String name = scanner.nextLine();
		printStream.print("Insert course CFU: ");
		String cfu = scanner.nextLine();
		controller.addCourse(new Course(id, name, cfu));
		courses.add(new Course(id, name, cfu));
	}

	public List<Student> getStudents() {
		return students;
	}

	public List<Course> getCourses() {
		return courses;
	}

	private void removeCourseToStudentCallController() {
		printStream.print(INSERT_STUDENT_ID);
		String studentId = scanner.nextLine();
		printStream.print(INSERT_COURSE_ID);
		String courseId = scanner.nextLine();
		Course courseToFind;
		try {
			courseToFind = courses.stream().filter(
					course -> course.getId().equals(courseId)).collect(Collectors.toList()).get(0);
		} catch (Exception e) {
			courseToFind = new Course(courseId, "", "");
		}
		controller.removeCourseFromStudent(new Student(studentId, ""), courseToFind);
	}

	private void addCourseToStudentCallController() {
		printStream.print(INSERT_STUDENT_ID);
		String studentId = scanner.nextLine();
		printStream.print(INSERT_COURSE_ID);
		String courseId = scanner.nextLine();
		controller.addCourseToStudent(new Student(studentId, ""), new Course(courseId, "", ""));
	}

	private void removeStudentCallController() {
		printStream.print(INSERT_STUDENT_ID);
		String id = scanner.nextLine();
		Student studentToFind;
		try {
			studentToFind = students.stream().filter(
					student -> student.getId().equals(id)).collect(Collectors.toList()).get(0);
		} catch (Exception e) {
			studentToFind = new Student(id, "");
		}
		controller.removeStudent(studentToFind);
	}

	private void addStudentCallController() {
		printStream.print(INSERT_STUDENT_ID);
		String id = scanner.nextLine();
		printStream.print("Insert student name: ");
		String name = scanner.nextLine();
		controller.addStudent(new Student(id, name));
		students.add(new Student(id, name));
	}

	private void showAllStudentsCallController() {
		controller.getAllStudents();
	}

	private void showAllCoursesCallController() {
		controller.getAllCourses();
	}
}
