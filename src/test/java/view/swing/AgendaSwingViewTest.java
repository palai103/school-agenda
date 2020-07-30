package view.swing;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import controller.AgendaController;
import model.Course;
import model.Student;

public class AgendaSwingViewTest extends AssertJSwingJUnitTestCase{
	@Mock
	private AgendaController agendaController;
	private AgendaSwingView agendaSwingView;
	private FrameFixture window;

	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);

		GuiActionRunner.execute(() -> {
			agendaSwingView = new AgendaSwingView();
			agendaSwingView.setAgendaController(agendaController);
			return agendaSwingView;
		});

		window = new FrameFixture(robot(), agendaSwingView);
		window.show();
	}

	@Test 
	@GUITest
	public void testControlsInitialStates() {
		// Labels check
		window.label("studentPanel");
		window.label("studentIDLabel");
		window.label("studentNameLabel");
		window.label("studentErrorNotAddedLabel");
		window.label("studentAddedLabel");
		window.label("studentErrorNotRemovedLabel");
		window.label("studentRemovedLabel");
		window.label("studentErrorNotAddedToCourseLabel");
		window.label("studentAddedToCourseLabel");
		window.label("studentErrorNotRemovedFromCourseLabel");
		window.label("studentRemovedFromCourseLabel");
		window.label("coursePanel");
		window.label("courseIDLabel");
		window.label("courseNameLabel");
		window.label("courseCFULabel");
		window.label("courseErrorNotAddedLabel");
		window.label("courseAddedLabel");
		window.label("courseErrorNotRemovedLabel");
		window.label("courseRemovedLabel");
		window.label("courseErrorNotAddedToStudentLabel");
		window.label("courseAddedToStudentLabel");
		window.label("courseErrorNotRemovedFromStudentLabel");
		window.label("courseRemovedFromStudentLabel");

		// Text fields check
		window.textBox("studentIDTextField").requireEnabled();
		window.textBox("studentNameTextField").requireEnabled();
		window.textBox("courseIDTextField").requireEnabled();
		window.textBox("courseNameTextField").requireEnabled();
		window.textBox("courseCFUTextField").requireEnabled();

		// Buttons check
		window.button("addNewStudentButton").requireDisabled();
		window.button("addStudentToCourseButton").requireDisabled();
		window.button("removeStudentButton").requireDisabled();
		window.button("removeStudentFromCourseButton").requireDisabled();
		window.button("addNewCourseButton").requireDisabled();
		window.button("addCourseToStudentButton").requireDisabled();
		window.button("removeCourseButton").requireDisabled();
		window.button("removeCourseFromStudentButton").requireDisabled();

		// Lists check
		window.list("studentsList");
		window.list("coursesList");		
	}

	@Test
	public void testWhenStudentIdAndStudentNameAreNotEmptyThenAddButtonShouldBeEnabled() {
		// setup
		window.textBox("studentIDTextField").enterText("1");
		window.textBox("studentNameTextField").enterText("test");

		// verify
		window.button("addNewStudentButton").requireEnabled();
	}

	@Test
	public void testWhenEitherStudentIdOrStudentNameAreBlankThenAddButtonShouldBeDisabled() {
		JTextComponentFixture studentIDTextField = window.textBox("studentIDTextField");
		JTextComponentFixture studentNameTextField = window.textBox("studentNameTextField");

		studentIDTextField.enterText("1");
		studentNameTextField.enterText(" ");
		window.button("addNewStudentButton").requireDisabled();

		// clean the fields
		studentIDTextField.setText("");
		studentNameTextField.setText("");

		studentIDTextField.enterText(" ");
		studentNameTextField.enterText("test");
		window.button("addNewStudentButton").requireDisabled();
	}

	@Test
	public void testRemoveStudentButtonShouldBeEnabledOnlyWhenAStudentIsSelected() {
		GuiActionRunner.execute(() ->
		agendaSwingView.getListStudentsModel().addElement(new Student("1", "test student")));
		window.list("studentsList").selectItem(0);
		JButtonFixture removeStudentButton = window.button("removeStudentButton");
		removeStudentButton.requireEnabled();

		window.list("studentsList").clearSelection();
		removeStudentButton.requireDisabled();
	}

	@Test
	public void testAddStudentToCourseAndAddCourseToStudentShouldBeEnabledWhenAStudentAndACourseAreSelected() {
		GuiActionRunner.execute(() -> {
			agendaSwingView.getListStudentsModel().addElement(new Student("1", "test student"));
			agendaSwingView.getListCoursesModel().addElement(new Course("1", "test course"));
		});
		window.list("studentsList").selectItem(0);
		window.list("coursesList").selectItem(0);
		JButtonFixture addCourseToStudentButton = window.button("addCourseToStudentButton");
		JButtonFixture addStudentToCourseButton = window.button("addStudentToCourseButton");

		addCourseToStudentButton.requireEnabled();
		addStudentToCourseButton.requireEnabled();

		window.list("studentsList").clearSelection();
		addStudentToCourseButton.requireDisabled();
		addCourseToStudentButton.requireDisabled();

		window.list("studentsList").selectItem(0);
		window.list("coursesList").clearSelection();
		addStudentToCourseButton.requireDisabled();
		addCourseToStudentButton.requireDisabled();
	}

	@Test
	public void testRemoveStudentFromCourseAndRemoveCourseFromStudentShouldBeEnabledWhenAStudentAndACourseAreSelected() {
		GuiActionRunner.execute(() -> {
			agendaSwingView.getListStudentsModel().addElement(new Student("1", "test student"));
			agendaSwingView.getListCoursesModel().addElement(new Course("1", "test course"));
		});
		window.list("studentsList").selectItem(0);
		window.list("coursesList").selectItem(0);
		JButtonFixture removeCourseFromStudentButton = window.button("removeCourseFromStudentButton");
		JButtonFixture removeStudentFromCourseButton = window.button("removeStudentFromCourseButton");

		removeCourseFromStudentButton.requireEnabled();
		removeStudentFromCourseButton.requireEnabled();

		window.list("studentsList").clearSelection();
		removeStudentFromCourseButton.requireDisabled();
		removeCourseFromStudentButton.requireDisabled();

		window.list("studentsList").selectItem(0);
		window.list("coursesList").clearSelection();
		removeStudentFromCourseButton.requireDisabled();
		removeCourseFromStudentButton.requireDisabled();
	}

	@Test
	public void testShowAllStudentsShouldAddStudentToTheList() {
		// setup
		Student testStudent1 = new Student("1", "test student 1");
		Student testStudent2 = new Student("2", "test student 2");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.showAllStudents(asList(testStudent1, testStudent2));
		});

		// verify
		String[] listContents = window.list("studentsList").contents();
		assertThat(listContents).containsExactly(testStudent1.toString(), testStudent2.toString());
	}

	@Test
	public void testNotifyStudentNotAddedShouldShowStudentErrorNotAddedMessage() {
		// setup
		Student testStudent = new Student("1", "test student");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyStudentNotAdded(testStudent);
		});

		// verify
		window.label("studentErrorNotAddedLabel").requireText("ERROR! " + testStudent.toString() + " NOT added!");
	}

	@Test
	public void testNotifyStudentAddedShouldAddTheStudentToTheListAndShowStudentAddedMessage() {
		// setup
		Student testStudent = new Student("1", "test student");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyStudentAdded(testStudent);
		});

		// verify
		String[] listContents = window.list("studentsList").contents();
		assertThat(listContents).containsExactly(testStudent.toString());
		window.label("studentAddedLabel").requireText(testStudent.toString() + " successfully added!");
	}

	@Test
	public void testNotifyStudentNotRemovedShouldShowStudentErrorNotRemovedMessage() {
		// setup
		Student testStudent = new Student("1", "test student");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyStudentNotRemoved(testStudent);
		});

		// verify
		window.label("studentErrorNotRemovedLabel").requireText(testStudent.toString() + " NOT removed!");
	}

	@Test
	public void testNotifyStudentRemovedShouldRemoveTheStudentFromTheListAndShowStudentRemovedMessage() {
		// setup
		Student testStudent1 = new Student("1", "test student 1");
		Student testStudent2 = new Student("1", "test student 2");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Student> listStudentsModel = agendaSwingView.getListStudentsModel();
			listStudentsModel.addElement(testStudent1);
			listStudentsModel.addElement(testStudent2);
		});

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyStudentRemoved(testStudent1);
		});

		// verify
		String[] listContents = window.list("studentsList").contents();
		assertThat(listContents).containsExactly(testStudent2.toString());
		window.label("studentRemovedLabel").requireText(testStudent1.toString() + " successfully removed!");
	}

	@Test
	public void testNotifyStudentNotAddedToCourseShouldShowStudentErrorNotAddedToCourseMessage() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyStudentNotAddedToCourse(testStudent, testCourse);
		});

		// verify
		window.label("studentErrorNotAddedToCourseLabel").requireText(testStudent.toString() + " NOT removed from " + testCourse.toString());
	}

	@Test
	public void testNotifyStudentAddedToCourseShouldShowStudentAddedToCourseMessage() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyStudentAddedToCourse(testStudent, testCourse);
		});

		// verify
		window.label("studentAddedToCourseLabel").requireText(testStudent.toString() + " added to " + testCourse.toString());
	}

	@Test
	public void testNotifyStudentNotRemovedFromCourseShouldShowStudentErrorNotRemovedFromCourseMessage() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyStudentNotRemovedFromCourse(testStudent, testCourse);
		});

		// verify
		window.label("studentErrorNotRemovedFromCourseLabel").requireText(testStudent.toString() + " NOT removed from " + testCourse.toString());
	}

	@Test
	public void testNotifyStudentRemovedFromCourseShouldShowStudentRemovedFromCourseMessage() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyStudentRemovedFromCourse(testStudent, testCourse);
		});

		// verify
		window.label("studentRemovedFromCourseLabel").requireText(testStudent.toString() + " removed from " + testCourse.toString());
	}

	@Test
	public void testWhenCourseIdAndCourseNameAndCourseCFUAreNotEmptyThenAddButtonShouldBeEnabled() {
		// setup
		window.textBox("courseIDTextField").enterText("1");
		window.textBox("courseNameTextField").enterText("test");
		window.textBox("courseCFUTextField").enterText("9");

		// verify
		window.button("addNewCourseButton").requireEnabled();
	}

	@Test
	public void testWhenEitherCourseIdOrCourseNameOrCourseCFUAreBlankThenAddButtonShouldBeDisabled() {
		JTextComponentFixture courseIDTextField = window.textBox("courseIDTextField");
		JTextComponentFixture courseNameTextField = window.textBox("courseNameTextField");
		JTextComponentFixture courseCFUTextField = window.textBox("courseCFUTextField");

		courseIDTextField.enterText("1");
		courseNameTextField.enterText("test");
		courseCFUTextField.enterText(" ");
		window.button("addNewCourseButton").requireDisabled();

		// clean the fields
		courseIDTextField.setText("");
		courseNameTextField.setText("");
		courseCFUTextField.setText("");

		courseIDTextField.enterText("1");
		courseNameTextField.enterText(" ");
		courseCFUTextField.enterText("9");
		window.button("addNewCourseButton").requireDisabled();

		// clean the fields
		courseIDTextField.setText("");
		courseNameTextField.setText("");
		courseCFUTextField.setText("");

		courseIDTextField.enterText(" ");
		courseNameTextField.enterText("test");
		courseCFUTextField.enterText("9");
		window.button("addNewCourseButton").requireDisabled();

		// clean the fields
		courseIDTextField.setText("");
		courseNameTextField.setText("");
		courseCFUTextField.setText("");

		courseIDTextField.enterText("1");
		courseNameTextField.enterText(" ");
		courseCFUTextField.enterText(" ");
		window.button("addNewCourseButton").requireDisabled();

		// clean the fields
		courseIDTextField.setText("");
		courseNameTextField.setText("");
		courseCFUTextField.setText("");

		courseIDTextField.enterText(" ");
		courseNameTextField.enterText("test");
		courseCFUTextField.enterText(" ");
		window.button("addNewCourseButton").requireDisabled();

		// clean the fields
		courseIDTextField.setText("");
		courseNameTextField.setText("");
		courseCFUTextField.setText("");

		courseIDTextField.enterText(" ");
		courseNameTextField.enterText(" ");
		courseCFUTextField.enterText("9");
		window.button("addNewCourseButton").requireDisabled();
	}















}
