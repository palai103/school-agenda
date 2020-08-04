package view;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import controller.AgendaController;
import model.Course;
import model.Student;

public class AgendaViewTest {

	@Mock
	private AgendaController controller;

	@InjectMocks
	private AgendaViewCli cliView;

	private ByteArrayOutputStream testOutput;
	private ByteArrayInputStream testInput;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		testOutput = new ByteArrayOutputStream();
		cliView = new AgendaViewCli(System.in, new PrintStream(testOutput));
		cliView.inject(controller);
	}

	@Test
	public void testMenu() {
		// exercise
		cliView.showMenu();

		// verify
		assertThat(testOutput.toString()).hasToString("--------- Pick a choice: ---------\r\n"
				+ "1) Show all students\r\n" + "2) Show all courses\r\n" + "3) Add a student\r\n"
				+ "4) Add a course\r\n" + "5) Enroll a student to a course (by student)\r\n"
				+ "6) Enroll a student to a course (by course)\r\n"
				+ "7) Delete a student enrollment (by student id)\r\n"
				+ "8) Delete a student enrollment (by course id)\r\n" + "9) Delete a student\r\n"
				+ "10) Delete a course\r\n" + "11) Exit\r\n" + "---------------------------------\r\n");
	}

	@Test
	public void testShowAllStudents() {
		// setup
		testInput = new ByteArrayInputStream("1".getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		verify(controller).getAllStudents();
	}

	@Test
	public void testPrintShowAllStudents() {
		// setup
		Student testStudent1 = new Student("1", "test student 1");
		Student testStudent2 = new Student("2", "test student 2");

		// exercise
		cliView.showAllStudents(asList(testStudent1, testStudent2));

		// verify
		assertThat(testOutput.toString())
				.hasToString("Student [id=1, name=test student 1]\r\n" + "Student [id=2, name=test student 2]\r\n");
	}

	@Test
	public void testNotifyStudentAdded() {
		// setup
		Student testStudent = new Student("1", "test student 1");

		// exercise
		cliView.notifyStudentAdded(testStudent);

		// verify
		assertThat(testOutput.toString()).hasToString("Added " + testStudent.toString() + "\r\n");
	}

	@Test
	public void testAddStudent() {
		// Setup
		String userInput = "3\n1\ntest student";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// Exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert student name: ");
		verify(controller).addStudent(new Student("1", "test student"));
	}

	@Test
	public void testNotifyStudentNotAdded() {
		// setup
		Student testStudent = new Student("1", "test student 1");

		// exercise
		cliView.notifyStudentNotAdded(testStudent);

		// verify
		assertThat(testOutput.toString()).hasToString(testStudent.toString() + " not added\r\n");
	}

	@Test
	public void testNotifyStudentRemoved() {
		// setup
		Student testStudent = new Student("1", "test student 1");

		// exercise
		cliView.notifyStudentRemoved(testStudent);

		// verify
		assertThat(testOutput.toString()).hasToString("Removed " + testStudent.toString() + "\r\n");
	}

	@Test
	public void testRemoveStudent() {
		// Setup
		String userInput = "9\n1";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// Exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: ");
		verify(controller).removeStudent(new Student("1", ""));
	}

	@Test
	public void testNotifyStudentNotRemoved() {
		// setup
		Student testStudent = new Student("1", "test student 1");

		// exercise
		cliView.notifyStudentNotRemoved(testStudent);

		// verify
		assertThat(testOutput.toString()).hasToString("Student with id " + testStudent.getId() + " not removed\r\n");
	}

	@Test
	public void testNotifyCourseAddedToStudent() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2");

		// exercise
		cliView.notifyCourseAddedToStudent(testStudent, testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString(
				"Course with id " + testCourse.getId() + " added to student with id " + testStudent.getId() + "\r\n");
	}

	@Test
	public void testAddCourseToStudent() {
		// setup
		String userInput = "5\n1\n2";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert course id: ");
		verify(controller).addCourseToStudent(new Student("1", ""), new Course("2", ""));
	}

	@Test
	public void testNotifyCourseNotAddedToStudent() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2");

		// exercise
		cliView.notifyCourseNotAddedToStudent(testStudent, testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString("Course with id " + testCourse.getId()
				+ " not added to student with id " + testStudent.getId() + "\r\n");
	}

	@Test
	public void testNotifyCourseRemovedFromStudent() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2");

		// exercise
		cliView.notifyCourseRemovedFromStudent(testStudent, testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString("Course with id " + testCourse.getId()
				+ " removed from student with id " + testStudent.getId() + "\r\n");
	}

	@Test
	public void testRemoveCourseFromStudent() {
		// setup
		String userInput = "7\n1\n2";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert course id: ");
		verify(controller).removeCourseFromStudent(new Student("1", ""), new Course("2", ""));
	}

	@Test
	public void testNotifyCourseNotRemovedFromStudent() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2");

		// exercise
		cliView.notifyCourseNotRemovedFromStudent(testStudent, testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString("Course with id " + testCourse.getId()
				+ " not removed from student with id " + testStudent.getId() + "\r\n");
	}

	@Test
	public void testNotifyCourseAdded() {
		// setup
		Course testCourse = new Course("1", "test course");

		// exercise
		cliView.notifyCourseAdded(testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString("Added " + testCourse.toString() + "\r\n");
	}

	@Test
	public void testAddCourse() {
		// Setup
		String userInput = "4\n1\ntest course";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// Exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert course id: Insert course name: ");
		verify(controller).addCourse(new Course("1", "test course"));
	}

	@Test
	public void testNotifyCourseNotAdded() {
		// setup
		Course testCourse = new Course("1", "test course");

		// exercise
		cliView.notifyCourseNotAdded(testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString(testCourse.toString() + " not added\r\n");
	}

	@Test
	public void testNotifyCourseRemoved() {
		// setup
		Course testCourse = new Course("1", "test course");

		// exercise
		cliView.notifyCourseRemoved(testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString("Removed " + testCourse.toString() + "\r\n");
	}

	@Test
	public void testRemoveCourse() {
		// Setup
		String userInput = "10\n1";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// Exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert course id: ");
		verify(controller).removeCourse(new Course("1", ""));
	}

	@Test
	public void testNotifyCourseNotRemoved() {
		// setup
		Course testCourse = new Course("1", "test course");

		// exercise
		cliView.notifyCourseNotRemoved(testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString(testCourse.toString() + " not removed\r\n");
	}

	@Test
	public void testNotifyStudentRemovedFromCourse() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2");

		// exercise
		cliView.notifyStudentRemovedFromCourse(testStudent, testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString("Student with id " + testStudent.getId()
				+ " removed from course with id " + testCourse.getId() + "\r\n");
	}

	@Test
	public void testNotifyStudentNotRemovedFromCourse() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2");

		// exercise
		cliView.notifyStudentNotRemovedFromCourse(testStudent, testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString("Student with id " + testStudent.getId()
				+ " not removed from course with id " + testCourse.getId() + "\r\n");
	}

	@Test
	public void testNotifyStudentNotAddedToCourse() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2");

		// exercise
		cliView.notifyStudentNotAddedToCourse(testStudent, testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString("Student with id " + testStudent.getId()
				+ " not added to course with id " + testCourse.getId() + "\r\n");
	}

	@Test
	public void testAddStudentToCourse() {
		// setup
		String userInput = "6\n1\n2";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert course id: ");
		verify(controller).addStudentToCourse(new Student("1", ""), new Course("2", ""));
	}

	@Test
	public void testNotifyStudentAddedToCourse() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2");

		// exercise
		cliView.notifyStudentAddedToCourse(testStudent, testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString(
				"Student with id " + testStudent.getId() + " added to course with id " + testCourse.getId() + "\r\n");
	}

	@Test
	public void testShowAllCourses() {
		// setup
		testInput = new ByteArrayInputStream("2".getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		verify(controller).getAllCourses();
	}

	@Test
	public void testPrintShowAllCourses() {
		// setup
		Course testCourse1 = new Course("1", "test course 1");
		Course testCourse2 = new Course("2", "test course 2");

		// exercise
		cliView.showAllCourses(asList(testCourse1, testCourse2));

		// verify
		assertThat(testOutput.toString())
				.hasToString("Course [id=1, name=test course 1]\r\n" + "Course [id=2, name=test course 2]\r\n");
	}

	@Test
	public void testRemoveStudentFromCourse() {
		// setup
		String userInput = "8\n1\n2";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert course id: ");
		verify(controller).removeStudentFromCourse(new Student("1", ""), new Course("2", ""));
	}
}
