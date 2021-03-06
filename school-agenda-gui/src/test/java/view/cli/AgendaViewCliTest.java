package view.cli;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import controller.AgendaController;
import model.Course;
import model.Student;

public class AgendaViewCliTest {

	@Mock
	private AgendaController controller;

	@InjectMocks
	private AgendaViewCli cliView;

	private ByteArrayOutputStream testOutput;
	private ByteArrayInputStream testInput;
	private static final String NEWLINE = System.getProperty("line.separator");

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		testOutput = new ByteArrayOutputStream();
		cliView = new AgendaViewCli(System.in, new PrintStream(testOutput));
		cliView.inject(controller);
	}

	@Test
	public void testMenu() {
		// setup
		// testing the switch case with input "0" so that it fulfills the Scanner need
		// for a line
		testInput = new ByteArrayInputStream("0".getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).hasToString("--------- Pick a choice: ---------" + NEWLINE
				+ "1) Show all students" + NEWLINE + "2) Show all courses" + NEWLINE + "3) Add a student" + NEWLINE
				+ "4) Add a course" + NEWLINE + "5) Enroll a student to a course (by student)" + NEWLINE
				+ "6) Enroll a student to a course (by course)" + NEWLINE
				+ "7) Delete a student enrollment (by student id)" + NEWLINE
				+ "8) Delete a student enrollment (by course id)" + NEWLINE + "9) Delete a student" + NEWLINE
				+ "10) Delete a course" + NEWLINE + "11) Show all student courses" + NEWLINE
				+ "12) Show all course students" + NEWLINE + "13) Exit" + NEWLINE + "---------------------------------"
				+ NEWLINE);
	}

	@Test
	public void testShowAllStudentCourses() {
		// setup
		testInput = new ByteArrayInputStream("11\n1".getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id:");
		verify(controller).getAllStudentCourses(new Student("1", ""));
	}

	@Test
	public void testShowAllStudentCoursesWithEmptyID() {
		// setup
		testInput = new ByteArrayInputStream("11\n \n1".getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert student id:");
		verify(controller).getAllStudentCourses(new Student("1", ""));
	}

	@Test
	public void testPrintShowAllStudentCourses() {
		// setup
		testInput = new ByteArrayInputStream("11\n1".getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.showAllStudentCourses(asList(new Course("1", "test course", "9")));

		// verify
		assertThat(testOutput.toString()).hasToString((new Course("1", "test course", "9").toString() + NEWLINE));
	}

	@Test
	public void testPrintShowAllStudentCoursesCallController() {
		// setup
		testInput = new ByteArrayInputStream("11\n1".getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id:");
	}

	@Test
	public void testShowAllCourseStudents() {
		// setup
		testInput = new ByteArrayInputStream("12\n1".getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert course id:");
		verify(controller).getAllCourseStudents(new Course("1", "", ""));
	}

	@Test
	public void testShowAllCourseStudentsWithEmptyID() {
		// setup
		testInput = new ByteArrayInputStream("12\n \n1".getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert course id: Insert course id:");
		verify(controller).getAllCourseStudents(new Course("1", "", ""));
	}

	@Test
	public void testPrintShowAllCourseStudents() {
		// setup
		testInput = new ByteArrayInputStream("12\n1".getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert course id:");
	}

	@Test
	public void testPrintShowAllCourseStudentsCallController() {
		// setup

		testInput = new ByteArrayInputStream("12\n1".getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.showAllCourseStudents(asList(new Student("1", "test student")));

		// verify
		assertThat(testOutput.toString()).hasToString((new Student("1", "test student").toString() + NEWLINE));
	}

	@Test
	public void testExit() {
		// setup
		testInput = new ByteArrayInputStream("13".getBytes());
		cliView.setInput(testInput);

		// exercise
		int code = cliView.menuChoice();

		// verify
		assertThat(code).isEqualTo(-1);
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
		assertThat(testOutput.toString()).hasToString(
				"Student [id=1, name=test student 1]" + NEWLINE + "Student [id=2, name=test student 2]" + NEWLINE);
	}

	@Test
	public void testNotifyStudentAdded() {
		// setup
		Student testStudent = new Student("1", "test student 1");

		// exercise
		cliView.notifyStudentAdded(testStudent);

		// verify
		assertThat(testOutput.toString()).hasToString("Added " + testStudent.toString() + NEWLINE);
	}

	@Test
	public void testAddStudent() {
		// setup
		String userInput = "3\n1\ntest student";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert student name: ");
		verify(controller).addStudent(new Student("1", "test student"));
	}
	
	@Test
	public void testShowAllStudentsShouldAddStudentsToTheList() {
		// setup
		Student testStudent1 = new Student("1", "test student 1");
		Student testStudent2 = new Student("2", "test student 2");

		// exercise
		cliView.showAllStudents(asList(testStudent1, testStudent2));

		// verify
		List<Student> listContents = cliView.getStudents();
		assertThat(listContents).containsExactly(testStudent1, testStudent2);
	}
	
	@Test
	public void testShowAllStudentsShouldAddStudentsToTheListClearBefore() {
		// setup
		Student testStudent1 = new Student("1", "test student 1");
		Student testStudent2 = new Student("2", "test student 2");
		cliView.getStudents().add(testStudent1);

		// exercise
		cliView.showAllStudents(asList(testStudent1, testStudent2));

		// verify
		List<Student> listContents = cliView.getStudents();
		assertThat(listContents).containsExactly(testStudent1, testStudent2);
	}
	
	@Test
	public void testShowAllCoursesShouldAddCoursessToTheList() {
		// setup
		Course testcourse1 = new Course("1", "test course 1", "9");
		Course testcourse2 = new Course("2", "test course 2", "9");

		// exercise
		cliView.showAllCourses(asList(testcourse1, testcourse2));

		// verify
		List<Course> listContents = cliView.getCourses();
		assertThat(listContents).containsExactly(testcourse1, testcourse2);
	}
	
	@Test
	public void testShowAllCoursesShouldAddCoursessToTheListClearBefore() {
		// setup
		Course testcourse1 = new Course("1", "test course 1", "9");
		Course testcourse2 = new Course("2", "test course 2", "9");
		cliView.getCourses().add(testcourse1);
		// exercise
		cliView.showAllCourses(asList(testcourse1, testcourse2));

		// verify
		List<Course> listContents = cliView.getCourses();
		assertThat(listContents).containsExactly(testcourse1, testcourse2);
	}

	@Test
	public void testAddStudentWithEmptyID() {
		// setup
		String userInput = "3\n \n1\ntest student";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert student id: Insert student name: ");
		verify(controller).addStudent(new Student("1", "test student"));
	}

	@Test
	public void testAddStudentWithEmptyName() {
		// setup
		String userInput = "3\n1\n \ntest student";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert student name: Insert student name: ");
		verify(controller).addStudent(new Student("1", "test student"));
	}

	@Test
	public void testAddCourseWithEmptyID() {
		// setup
		String userInput = "4\n \n1\ntest course\n9";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString())
				.contains("Insert course id: Insert course id: Insert course name: Insert course CFU: ");
		verify(controller).addCourse(new Course("1", "test course", "9"));
	}

	@Test
	public void testAddCourseWithEmptyName() {
		// setup
		String userInput = "4\n1\n \ntest course\n9";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString())
				.contains("Insert course id: Insert course name: Insert course name: Insert course CFU: ");
		verify(controller).addCourse(new Course("1", "test course", "9"));
	}

	@Test
	public void testAddCourseWithEmptyCfu() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
		String userInput = "4\n1\ntest course\n \n9";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString())
				.contains("Insert course id: Insert course name: Insert course CFU: Insert course CFU: ");
		verify(controller).addCourse(testCourse);
	}

	@Test
	public void testAddCourseWithNotAllowedCfu() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
		String userInput = "4\n1\ntest course\n333\n9";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		verify(controller).addCourse(testCourse);
	}

	@Test
	public void testNotifyStudentNotAdded() {
		// setup
		Student testStudent = new Student("1", "test student 1");

		// exercise
		cliView.notifyStudentNotAdded(testStudent);

		// verify
		assertThat(testOutput.toString()).hasToString(testStudent.toString() + " not added" + NEWLINE);
	}

	@Test
	public void testNotifyStudentRemoved() {
		// setup
		Student testStudent = new Student("1", "test student 1");

		// exercise
		cliView.notifyStudentRemoved(testStudent);

		// verify
		assertThat(testOutput.toString()).hasToString("Student with id " + testStudent.getId() + " removed" + NEWLINE);
	}

	@Test
	public void testRemoveStudent() {
		// Setup
		String userInput = "9\n1";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);
		cliView.getStudents().add(new Student("1", "test student"));

		// Exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: ");
		verify(controller).removeStudent(new Student("1", "test student"));
	}

	@Test
	public void testRemoveStudentWhenStudentDoesNotExist() {
		// Setup
		String userInput = "9\n2";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);
		cliView.getStudents().add(new Student("1", "test student"));

		// Exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: ");
		verify(controller).removeStudent(new Student("2", ""));
	}

	@Test
	public void testRemoveStudentWithEmptyId() {
		// Setup
		String userInput = "9\n \n1";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);
		cliView.getStudents().add(new Student("1", "test student"));

		// Exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert student id: ");
		verify(controller).removeStudent(new Student("1", "test student"));
	}

	@Test
	public void testNotifyStudentNotRemoved() {
		// setup
		Student testStudent = new Student("1", "test student 1");

		// exercise
		cliView.notifyStudentNotRemoved(testStudent);

		// verify
		assertThat(testOutput.toString())
				.hasToString("Student with id " + testStudent.getId() + " not removed" + NEWLINE);
	}

	@Test
	public void testNotifyCourseAddedToStudent() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2", "9");

		// exercise
		cliView.notifyCourseAddedToStudent(testStudent, testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString(
				"Course with id " + testCourse.getId() + " added to student with id " + testStudent.getId() + NEWLINE);
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
		verify(controller).addCourseToStudent(new Student("1", ""), new Course("2", "", ""));
	}

	@Test
	public void testAddCourseToStudentWhenCourseExist() {
		// setup
		Course testCourse = new Course("2", "test course", "9");
		String userInput = "5\n1\n2";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);
		cliView.getCourses().add(testCourse);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert course id: ");
		verify(controller).addCourseToStudent(new Student("1", ""), testCourse);
	}

	@Test
	public void testAddCourseToStudentWhenCourseDoesNotExist() {
		// setup
		Course testCourse = new Course("2", "test course", "9");
		Course testCourseToFind = new Course("1", "", "");
		String userInput = "5\n1\n1";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);
		cliView.getCourses().add(testCourse);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert course id: ");
		verify(controller).addCourseToStudent(new Student("1", ""), testCourseToFind);
	}

	@Test
	public void testAddCourseToStudentWithEmptyStudentId() {
		// setup
		String userInput = "5\n \n1\n2";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert student id: Insert course id: ");
		verify(controller).addCourseToStudent(new Student("1", ""), new Course("2", "", ""));
	}

	@Test
	public void testAddCourseToStudentWithEmptyCourseId() {
		// setup
		String userInput = "5\n1\n \n2";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert course id: Insert course id: ");
		verify(controller).addCourseToStudent(new Student("1", ""), new Course("2", "", ""));
	}

	@Test
	public void testNotifyCourseNotAddedToStudent() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2", "9");

		// exercise
		cliView.notifyCourseNotAddedToStudent(testStudent, testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString("Course with id " + testCourse.getId()
				+ " not added to student with id " + testStudent.getId() + NEWLINE);
	}

	@Test
	public void testNotifyCourseRemovedFromStudent() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2", "9");

		// exercise
		cliView.notifyCourseRemovedFromStudent(testStudent, testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString("Course with id " + testCourse.getId()
				+ " removed from student with id " + testStudent.getId() + NEWLINE);
	}

	@Test
	public void testRemoveCourseFromStudent() {
		// setup
		String userInput = "7\n1\n2";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);
		Student testStudent = new Student("1", "");
		Course testCourse = new Course("2", "test course", "9");
		cliView.getStudents().add(testStudent);
		cliView.getCourses().add(testCourse);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert course id: ");
		verify(controller).removeCourseFromStudent(new Student("1", ""), testCourse);
	}

	@Test
	public void testRemoveCourseFromStudentWhenCourseDoesNotExist() {
		// setup
		String userInput = "7\n1\n3";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);
		Student testStudent = new Student("1", "");
		Course testCourse = new Course("2", "test course", "9");
		cliView.getStudents().add(testStudent);
		cliView.getCourses().add(testCourse);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert course id: ");
		verify(controller).removeCourseFromStudent(new Student("1", ""), new Course("3", "", ""));
	}

	@Test
	public void testRemoveCourseFromStudentWithEmptyStudentId() {
		// setup
		String userInput = "7\n \n1\n2";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert student id: Insert course id: ");
		verify(controller).removeCourseFromStudent(new Student("1", ""), new Course("2", "", ""));
	}

	@Test
	public void testRemoveCourseFromStudentWithEmptyCourseId() {
		// setup
		String userInput = "7\n1\n \n2";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert course id: Insert course id: ");
		verify(controller).removeCourseFromStudent(new Student("1", ""), new Course("2", "", ""));
	}

	@Test
	public void testNotifyCourseNotRemovedFromStudent() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2", "9");

		// exercise
		cliView.notifyCourseNotRemovedFromStudent(testStudent, testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString("Course with id " + testCourse.getId()
				+ " not removed from student with id " + testStudent.getId() + NEWLINE);
	}

	@Test
	public void testNotifyCourseAdded() {
		// setup
		Course testCourse = new Course("1", "test course", "9");

		// exercise
		cliView.notifyCourseAdded(testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString("Added " + testCourse.toString() + "" + NEWLINE);
	}

	@Test
	public void testAddCourse() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
		String userInput = "4\n1\ntest course\n9";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert course id: Insert course name: Insert course CFU: ");
		verify(controller).addCourse(testCourse);
	}

	@Test
	public void testNotifyCourseNotAdded() {
		// setup
		Course testCourse = new Course("1", "test course", "9");

		// exercise
		cliView.notifyCourseNotAdded(testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString(testCourse.toString() + " not added" + NEWLINE);
	}

	@Test
	public void testNotifyCourseRemoved() {
		// setup
		Course testCourse = new Course("1", "test course", "9");

		// exercise
		cliView.notifyCourseRemoved(testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString("Course with id " + testCourse.getId() + " removed" + NEWLINE);
	}

	@Test
	public void testRemoveCourse() {
		// Setup
		String userInput = "10\n1";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);
		cliView.getCourses().add(new Course("1", "test course", "9"));

		// Exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert course id: ");
		verify(controller).removeCourse(new Course("1", "test course", "9"));
	}

	@Test
	public void testRemoveCourseWhenCouseDoesNotExist() {
		// Setup
		String userInput = "10\n2";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);
		cliView.getCourses().add(new Course("1", "test course", "9"));

		// Exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert course id: ");
		verify(controller).removeCourse(new Course("2", "", ""));
	}

	@Test
	public void testRemoveCourseWithEmptyCourseId() {
		// Setup
		String userInput = "10\n \n1";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);
		cliView.getCourses().add(new Course("1", "test course", "9"));

		// Exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert course id: Insert course id: ");
		verify(controller).removeCourse(new Course("1", "test course", "9"));
	}

	@Test
	public void testNotifyCourseNotRemoved() {
		// setup
		Course testCourse = new Course("1", "test course", "9");

		// exercise
		cliView.notifyCourseNotRemoved(testCourse);

		// verify
		assertThat(testOutput.toString())
				.hasToString("Course with id " + testCourse.getId() + " not removed" + NEWLINE);
	}

	@Test
	public void testNotifyStudentRemovedFromCourse() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2", "9");

		// exercise
		cliView.notifyStudentRemovedFromCourse(testStudent, testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString("Student with id " + testStudent.getId()
				+ " removed from course with id " + testCourse.getId() + NEWLINE);
	}

	@Test
	public void testNotifyStudentNotRemovedFromCourse() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2", "9");

		// exercise
		cliView.notifyStudentNotRemovedFromCourse(testStudent, testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString("Student with id " + testStudent.getId()
				+ " not removed from course with id " + testCourse.getId() + NEWLINE);
	}

	@Test
	public void testNotifyStudentNotAddedToCourse() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2", "9");

		// exercise
		cliView.notifyStudentNotAddedToCourse(testStudent, testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString("Student with id " + testStudent.getId()
				+ " not added to course with id " + testCourse.getId() + NEWLINE);
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
		verify(controller).addStudentToCourse(new Student("1", ""), new Course("2", "", ""));
	}

	@Test
	public void testAddStudentToCourseWhenStudentExist() {
		// setup
		Student testStudent = new Student("1", "test student");
		String userInput = "6\n1\n2";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);
		cliView.getStudents().add(testStudent);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert course id: ");
		verify(controller).addStudentToCourse(testStudent, new Course("2", "", ""));
	}

	@Test
	public void testAddStudentToCourseWhenStudentDoesNotExist() {
		// setup
		Student testStudent = new Student("1", "test student");
		Student testStudentToFind = new Student("2", "");
		String userInput = "6\n2\n2";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);
		cliView.getStudents().add(testStudent);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert course id: ");
		verify(controller).addStudentToCourse(testStudentToFind, new Course("2", "", ""));
	}

	@Test
	public void testAddStudentToCourseWithEmptyStudentId() {
		// setup
		String userInput = "6\n \n1\n2";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert student id: Insert course id: ");
		verify(controller).addStudentToCourse(new Student("1", ""), new Course("2", "", ""));
	}

	@Test
	public void testAddStudentToCourseWithEmptyCourseId() {
		// setup
		String userInput = "6\n1\n \n2";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert course id: Insert course id: ");
		verify(controller).addStudentToCourse(new Student("1", ""), new Course("2", "", ""));
	}

	@Test
	public void testNotifyStudentAddedToCourse() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2", "9");

		// exercise
		cliView.notifyStudentAddedToCourse(testStudent, testCourse);

		// verify
		assertThat(testOutput.toString()).hasToString(
				"Student with id " + testStudent.getId() + " added to course with id " + testCourse.getId() + NEWLINE);
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
		Course testCourse1 = new Course("1", "test course 1", "9");
		Course testCourse2 = new Course("2", "test course 2", "9");

		// exercise
		cliView.showAllCourses(asList(testCourse1, testCourse2));

		// verify
		assertThat(testOutput.toString()).hasToString("Course [id=1, name=test course 1, CFU=9]" + NEWLINE
				+ "Course [id=2, name=test course 2, CFU=9]" + NEWLINE);
	}

	@Test
	public void testRemoveStudentFromCourse() {
		// setup
		String userInput = "8\n1\n2";
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("2", "", "");
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);
		cliView.getStudents().add(testStudent);
		cliView.getCourses().add(testCourse);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert course id: ");
		verify(controller).removeStudentFromCourse(testStudent, testCourse);
	}

	@Test
	public void testRemoveStudentFromCourseWhenStudentDoesNotExist() {
		// setup
		String userInput = "8\n3\n2";
		Student testStudentToFind = new Student("3", "");
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("2", "", "");
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);
		cliView.getStudents().add(testStudent);
		cliView.getCourses().add(testCourse);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert course id: ");
		verify(controller).removeStudentFromCourse(testStudentToFind, testCourse);
	}

	@Test
	public void testRemoveStudentFromCourseWithEmptyStudentId() {
		// setup
		String userInput = "8\n \n1\n2";
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("2", "", "");
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);
		cliView.getStudents().add(testStudent);
		cliView.getCourses().add(testCourse);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert student id: Insert course id: ");
		verify(controller).removeStudentFromCourse(testStudent, testCourse);
	}

	@Test
	public void testRemoveStudentFromCourseWithEmptyCourseId() {
		// setup
		String userInput = "8\n1\n \n2";
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("2", "", "");
		testInput = new ByteArrayInputStream(userInput.getBytes());
		cliView.setInput(testInput);
		cliView.getStudents().add(testStudent);
		cliView.getCourses().add(testCourse);

		// exercise
		cliView.menuChoice();

		// verify
		assertThat(testOutput.toString()).contains("Insert student id: Insert course id: Insert course id: ");
		verify(controller).removeStudentFromCourse(testStudent, testCourse);
	}
}
