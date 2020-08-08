package view.swing;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.awt.Frame;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.driver.FrameDriver;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.AbstractContainerFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.fixture.JTabbedPaneFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
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
	private JPanelFixture contentPanel;
	private JPanelFixture coursesPanel;

	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);

		GuiActionRunner.execute(() -> {
			agendaSwingView = new AgendaSwingView();
			agendaSwingView.setAgendaController(agendaController);
			return agendaSwingView;
		});

		window = new FrameFixture(robot(), agendaSwingView);
		window.show();
		
		contentPanel = window.panel("contentPane");
		coursesPanel = contentPanel.panel("studentTab");
	}
	
	private void getCoursesPanel() {
		JTabbedPaneFixture tabPanel = contentPanel.tabbedPane("tabbedPane");
		tabPanel.selectTab("Courses");
		coursesPanel = contentPanel.panel("courseTab");
	}
	
	private void getStudentsPanel() {
		JTabbedPaneFixture tabPanel = contentPanel.tabbedPane("tabbedPane");
		tabPanel.selectTab("Students");
		coursesPanel = contentPanel.panel("studentTab");
	}

	@Test 
	@GUITest
	public void testControlsInitialStates() {
		// Labels check
		window.label("studentIDLabel");
		window.label("studentNameLabel");
		window.label("studentMessageLabel");
		

		// Text fields check
		window.textBox("studentIDTextField").requireEnabled();
		window.textBox("studentNameTextField").requireEnabled();
		

		// Buttons check
		window.button("addNewStudentButton").requireDisabled();
		window.button("addCourseToStudentButton").requireDisabled();
		window.button("removeStudentButton").requireDisabled();
		window.button("removeCourseFromStudentButton").requireDisabled();
		
		// Lists check
		window.list("studentsList");
		window.list("studentCoursesList");
			
		getCoursesPanel();
		
		// Labels check
		window.label("courseIDLabel");
		window.label("courseNameLabel");
		window.label("courseCFULabel");
		window.label("courseMessageLabel");
		
		// Text fields check
		window.textBox("courseIDTextField").requireEnabled();
		window.textBox("courseNameTextField").requireEnabled();
		window.textBox("courseCFUTextField").requireEnabled();
		
		// Buttons check
		window.button("addNewCourseButton").requireDisabled();
		window.button("addStudentToCourseButton").requireDisabled();
		window.button("removeCourseButton").requireDisabled();
		window.button("removeStudentFromCourseButton").requireDisabled();
		
		// Lists check
		window.list("coursesList");
		window.list("courseStudentsList");
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
		GuiActionRunner.execute(() -> agendaSwingView.getListStudentsModel().addElement(new Student("1", "test student")));
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
			agendaSwingView.getListCoursesModel().addElement(new Course("1", "test course", "9"));
		});
		window.list("studentsList").selectItem(0);
		getCoursesPanel();
		window.list("coursesList").selectItem(0);
		getStudentsPanel();
		JButtonFixture addCourseToStudentButton = window.button("addCourseToStudentButton");
		getCoursesPanel();
		JButtonFixture addStudentToCourseButton = window.button("addStudentToCourseButton");
		getStudentsPanel();

		addCourseToStudentButton.requireEnabled();
		getCoursesPanel();
		addStudentToCourseButton.requireEnabled();
		getStudentsPanel();

		window.list("studentsList").clearSelection();
		addStudentToCourseButton.requireDisabled();
		getCoursesPanel();
		addCourseToStudentButton.requireDisabled();
		getStudentsPanel();

		window.list("studentsList").selectItem(0);
		getCoursesPanel();
		window.list("coursesList").clearSelection();
		getStudentsPanel();
		addStudentToCourseButton.requireDisabled();
		getCoursesPanel();
		addCourseToStudentButton.requireDisabled();
		getStudentsPanel();
		
		window.list("studentsList").clearSelection();
		addStudentToCourseButton.requireDisabled();
		getCoursesPanel();
		addCourseToStudentButton.requireDisabled();
	}
	
	@Test
	public void testRemoveCourseFromStudentButtonShouldBeEnabledWhenAStudentCourseIsSelected() {
		GuiActionRunner.execute(() -> {
			agendaSwingView.getListStudentsModel().addElement(new Student("1", "test student"));
			agendaSwingView.getListStudentCoursesModel().addElement(new Course("1", "student test course", "9"));
		});
		window.list("studentsList").selectItem(0);
		window.list("studentCoursesList").selectItem(0);
		JButtonFixture removeCourseFromStudentButton = window.button("removeCourseFromStudentButton");
		
		removeCourseFromStudentButton.requireEnabled();
		
		window.list("studentCoursesList").clearSelection();
		removeCourseFromStudentButton.requireDisabled();
	}
	
	@Test
	public void testRemoveStudentFromCourseButtonShouldBeEnabledWhenACourseStudentIsSelected() {
		getCoursesPanel();
		
		GuiActionRunner.execute(() -> {
			agendaSwingView.getListCoursesModel().addElement(new Course("1", "test course", "9"));
			agendaSwingView.getListCourseStudentsModel().addElement(new Student("1", "course test student"));
		});
		window.list("coursesList").selectItem(0);
		window.list("courseStudentsList").selectItem(0);
		JButtonFixture removeStudentFromCourse = window.button("removeStudentFromCourseButton");
		
		removeStudentFromCourse.requireEnabled();
		
		window.list("courseStudentsList").clearSelection();
		removeStudentFromCourse.requireDisabled();
	}

	@Test
	public void testShowAllStudentsShouldAddStudentsToTheList() {
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
	public void testShowAllStudentCoursesShouldAddStudentCoursesToTheList() {
		// setup
		Course testCourse1 = new Course("1", "student test course 1", "9");
		Course testCourse2 = new Course("2", "student test course 2", "9");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.showAllStudentCourses(asList(testCourse1, testCourse2));
		});

		// verify
		String[] listContents = window.list("studentCoursesList").contents();
		assertThat(listContents).containsExactly(testCourse1.toString(), testCourse2.toString());
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
		assertThat(window.list("studentsList").contents()).isEmpty();
		window.label("studentMessageLabel").requireText("ERROR! " + testStudent.toString() + " NOT added!");
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
		window.label("studentMessageLabel").requireText(testStudent.toString() + " successfully added!");
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
		window.label("studentMessageLabel").requireText("ERROR! " + testStudent.toString() + " NOT removed!");
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
		window.label("studentMessageLabel").requireText(testStudent1.toString() + " successfully removed!");
	}

	@Test
	public void testNotifyCourseNotAddedToStudentShouldShowCourseErrorNotAddedToStudentMessage() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course", "9");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyCourseNotAddedToStudent(testStudent, testCourse);
		});

		// verify
		assertThat(window.list("studentCoursesList").contents()).isEmpty();
		window.label("studentMessageLabel").requireText("ERROR! " + testCourse.toString() + " NOT added to " + testStudent.toString());
	}

	@Test
	public void testNotifyCourseAddedToStudentShouldAddToTheListAndShowCourseAddedToStudentMessage() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course", "9");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyCourseAddedToStudent(testStudent, testCourse);
		});

		// verify
		assertThat(window.list("studentCoursesList").contents()).containsExactly(testCourse.toString());
		window.label("studentMessageLabel").requireText(testCourse.toString() + " added to " + testStudent.toString());
	}

	@Test
	public void testNotifyCourseNotRemovedFromStudentShouldShowCourseErrorNotRemovedFromStudentMessage() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course", "9");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyCourseNotRemovedFromStudent(testStudent, testCourse);
		});

		// verify
		window.label("studentMessageLabel").requireText("ERROR! " + testCourse.toString() + " NOT removed from " + testStudent.toString());
	}

	@Test
	public void testNotifyCourseRemovedFromStudentShouldRemoveFromTheListAndShowCourseRemovedFromStudentMessage() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse1 = new Course("1", "student test course 1", "9");
		Course testCourse2 = new Course("2", "student test course 2", "9");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.getListStudentCoursesModel().addElement(testCourse1);
			agendaSwingView.getListStudentCoursesModel().addElement(testCourse2);
			agendaSwingView.notifyCourseRemovedFromStudent(testStudent, testCourse1);
		});

		// verify
		String[] listContents = window.list("studentCoursesList").contents();
		assertThat(listContents).containsExactly(testCourse2.toString());
		window.label("studentMessageLabel").requireText(testCourse1.toString() + " removed from " + testStudent.toString());
	}

	@Test
	public void testWhenCourseIdAndCourseNameAndCourseCFUAreNotEmptyThenAddButtonShouldBeEnabled() {
		// setup
		getCoursesPanel();
		window.textBox("courseIDTextField").enterText("1");
		window.textBox("courseNameTextField").enterText("test");
		window.textBox("courseCFUTextField").enterText("9");

		// verify
		window.button("addNewCourseButton").requireEnabled();
	}
	
	@Test
	public void testWhenCourseCFUIsNotANumberThenAddCourseButtonShouldBeDisabled() {
		getCoursesPanel();
		
		JTextComponentFixture courseIDTextField = window.textBox("courseIDTextField");
		JTextComponentFixture courseNameTextField = window.textBox("courseNameTextField");
		JTextComponentFixture courseCFUTextField = window.textBox("courseCFUTextField");

		courseIDTextField.enterText("1");
		courseNameTextField.enterText("test");
		courseCFUTextField.enterText("definitely not a number");
		
		window.button("addNewCourseButton").requireDisabled();
	}

	@Test
	public void testWhenEitherCourseIdOrCourseNameOrCourseCFUAreBlankThenAddButtonShouldBeDisabled() {
		getCoursesPanel();
		
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

	@Test
	public void testRemoveCourseButtonShouldBeEnabledOnlyWhenACourseIsSelected() {
		getCoursesPanel();
		
		GuiActionRunner.execute(() -> agendaSwingView.getListCoursesModel().addElement(new Course("1", "test course", "9")));
		window.list("coursesList").selectItem(0);
		JButtonFixture removeCourseButton = window.button("removeCourseButton");
		removeCourseButton.requireEnabled();

		window.list("coursesList").clearSelection();
		removeCourseButton.requireDisabled();
	}

	@Test
	public void testShowAllCoursesShouldAddCoursesToTheList() {
		// setup
		getCoursesPanel();
		Course testCourse1 = new Course("1", "test course 1", "9");
		Course testCourse2 = new Course("2", "test course 2", "9");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.showAllCourses(asList(testCourse1, testCourse2));
		});

		// verify
		String[] listContents = window.list("coursesList").contents();
		assertThat(listContents).containsExactly(testCourse1.toString(), testCourse2.toString());
	}
	
	@Test
	public void testShowAllCourseStudentsShouldAddCourseStudentsToTheList() {
		// setup
		getCoursesPanel();
		Student testStudent1 = new Student("1", "course test student 1");
		Student testStudent2 = new Student("2", "course test student 2");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.showAllCourseStudents(asList(testStudent1, testStudent2));
		});

		// verify
		String[] listContents = window.list("courseStudentsList").contents();
		assertThat(listContents).containsExactly(testStudent1.toString(), testStudent2.toString());
	}

	@Test
	public void testNotifyCourseNotAddedShouldShowCourseErrorNotAddedMessage() {
		// setup
		getCoursesPanel();
		Course testCourse = new Course("1", "test course", "9");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyCourseNotAdded(testCourse);
		});

		// verify
		window.label("courseMessageLabel").requireText("ERROR! " + testCourse.toString() + " NOT added!");
	}

	@Test
	public void testNotifyCourseAddedShouldAddTheStudentToTheListAndShowStudentMessage() {
		// setup
		getCoursesPanel();
		Course testCourse = new Course("1", "test course", "9");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyCourseAdded(testCourse);
		});

		// verify
		String[] listContents = window.list("coursesList").contents();
		assertThat(listContents).containsExactly(testCourse.toString());
		window.label("courseMessageLabel").requireText(testCourse.toString() + " successfully added!");
	}

	@Test
	public void testNotifyCourseNotRemovedShouldShowCourseErrorNotRemovedMessage() {
		// setup
		getCoursesPanel();
		Course testCourse = new Course("1", "test course", "9");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyCourseNotRemoved(testCourse);
		});

		// verify
		window.label("courseMessageLabel").requireText("ERROR! " + testCourse.toString() + " NOT removed!");
	}

	@Test
	public void testNotifyCourseRemovedShouldRemoveTheCourseFromTheListAndShowCourseRemovedMessage() {
		// setup
		getCoursesPanel();
		Course testCourse1 = new Course("1", "test course 1", "9");
		Course testCourse2 = new Course("2", "test course 2", "9");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Course> listCoursesModel = agendaSwingView.getListCoursesModel();
			listCoursesModel.addElement(testCourse1);
			listCoursesModel.addElement(testCourse2);
		});

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyCourseRemoved(testCourse1);
		});

		// verify
		String[] listContents = window.list("coursesList").contents();
		assertThat(listContents).containsExactly(testCourse2.toString());
		window.label("courseMessageLabel").requireText(testCourse1.toString() + " successfully removed!");
	}

	@Test
	public void testNotifyStudentNotAddedToCourseShouldShowStudentErrorNotAddedToCourseMessage() {
		// setup
		getCoursesPanel();
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent = new Student("1", "test student");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyStudentNotAddedToCourse(testStudent, testCourse);
		});

		// verify
		window.label("courseMessageLabel").requireText("ERROR! " + testStudent.toString() + " NOT added to " + testCourse.toString());
	}

	@Test
	public void testNotifyStudentAddedToCourseShouldAddToTheListAndShowCourseAddedToStudentMessage() {
		// setup
		getCoursesPanel();
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent = new Student("1", "test student");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyStudentAddedToCourse(testStudent, testCourse);
		});

		// verify
		assertThat(window.list("courseStudentsList").contents()).containsExactly(testStudent.toString());
		window.label("courseMessageLabel").requireText(testStudent.toString() + " added to " + testCourse.toString());
	}

	@Test
	public void testNotifyStudentNotRemovedFromCourseShouldShowStudentErrorNotRemovedFromCourseMessage() {
		// setup
		getCoursesPanel();
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent = new Student("1", "test student");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.notifyStudentNotRemovedFromCourse(testStudent, testCourse);
		});

		// verify
		window.label("courseMessageLabel").requireText("ERROR! " + testStudent.toString() + " NOT removed from " + testCourse.toString());
	}

	@Test
	public void testNotifyStudentRemovedFromCourseShouldRemoveFromTheListAndShowSTudentRemovedFromCourseMessage() {
		// setup
		getCoursesPanel();
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent1 = new Student("1", "course test student 1");
		Student testStudent2 = new Student("2", "course test student 2");

		// exercise
		GuiActionRunner.execute(() -> {
			agendaSwingView.getListCourseStudentsModel().addElement(testStudent1);
			agendaSwingView.getListCourseStudentsModel().addElement(testStudent2);
			agendaSwingView.notifyStudentRemovedFromCourse(testStudent1, testCourse);
		});

		// verify
		String[] listContents = window.list("courseStudentsList").contents();
		assertThat(listContents).containsExactly(testStudent2.toString());
		window.label("courseMessageLabel").requireText(testStudent1.toString() + " removed from " + testCourse.toString());
	}
	
	@Test
	public void testAddNewStudentButtonShouldDelegateToAgendaControllerAddStudent() {
		window.textBox("studentIDTextField").enterText("1");
		window.textBox("studentNameTextField").enterText("test student");
		window.button("addNewStudentButton").click();
		
		verify(agendaController).addStudent(new Student("1", "test student"));
	}

	@Test
	public void testAddStudentToCourseButtonShouldDelegateToAgendaControllerAddStudentToCourse() {			
		GuiActionRunner.execute(() -> {
			agendaSwingView.getListStudentsModel().addElement(new Student("1", "test student"));
			agendaSwingView.getListCoursesModel().addElement(new Course("1", "test course", "9"));
		});
		
		window.list("studentsList").selectItem(0);
		getCoursesPanel();
		window.list("coursesList").selectItem(0);
		getCoursesPanel();
		window.button("addStudentToCourseButton").click();
		
		verify(agendaController).addStudentToCourse(new Student("1", "test student"), new Course("1", "test course", "9"));
	}
	
	@Test
	public void testRemoveStudentButtonShouldDelegateToAgendaControllerRemoveStudent() {
		Student testStudent1 = new Student("1", "test student 1");
		Student testStudent2 = new Student("2", "test student 2");
		GuiActionRunner.execute(() -> {
			agendaSwingView.getListStudentsModel().addElement(testStudent1);
			agendaSwingView.getListStudentsModel().addElement(testStudent2);
		});
		window.list("studentsList").selectItem(1);
		window.button("removeStudentButton").click();
		
		verify(agendaController).removeStudent(testStudent2);
	}
	
	@Test
	public void testRemoveStudentFromCourseButtonSHouldDelegateToAgendaControllerRemoveStudentFromCourse() {
		getCoursesPanel();
		
		GuiActionRunner.execute(() -> {
			agendaSwingView.getListCourseStudentsModel().addElement(new Student("1", "test student"));
			agendaSwingView.getListCoursesModel().addElement(new Course("1", "test course", "9"));
		});
		window.list("courseStudentsList").selectItem(0);
		window.list("coursesList").selectItem(0);
		window.button("removeStudentFromCourseButton").click();
		
		verify(agendaController).removeStudentFromCourse(new Student("1", "test student"), new Course("1", "test course", "9"));
	}
	
	@Test
	public void testAddNewCourseButtonShouldDelegateToAgendaControllerAddCourse() {
		getCoursesPanel();
		
		window.textBox("courseIDTextField").enterText("1");
		window.textBox("courseNameTextField").enterText("test course");
		window.textBox("courseCFUTextField").enterText("9");
		window.button("addNewCourseButton").click();
		
		verify(agendaController).addCourse(new Course("1", "test course", "9"));
	}
	
	@Test
	public void testAddCourseToStudentButtonShouldDelegateToAgendaControllerAddCourseToStudent() {		
		GuiActionRunner.execute(() -> {
			agendaSwingView.getListStudentsModel().addElement(new Student("1", "test student"));
			agendaSwingView.getListCoursesModel().addElement(new Course("1", "test course", "9"));
		});
		window.list("studentsList").selectItem(0);
		getCoursesPanel();
		window.list("coursesList").selectItem(0);
		getStudentsPanel();
		window.button("addCourseToStudentButton").click();
		
		verify(agendaController).addCourseToStudent(new Student("1", "test student"), new Course("1", "test course", "9"));
	}
	
	@Test
	public void testRemoveCourseButtonShouldDelegateToAgendaControllerRemoveCourse() {
		getCoursesPanel();
		
		Course testCourse1 = new Course("1", "test course 1", "9");
		Course testCourse2 = new Course("2", "test course 2", "9");
		GuiActionRunner.execute(() -> {
			agendaSwingView.getListCoursesModel().addElement(testCourse1);
			agendaSwingView.getListCoursesModel().addElement(testCourse2);
		});
		window.list("coursesList").selectItem(1);
		window.button("removeCourseButton").click();
		
		verify(agendaController).removeCourse(testCourse2);
	}
	
	@Test
	public void testRemoveCourseFromStudentButtonShouldDelegateToAgendaControllerRemoveCourseFromStudent() {
		GuiActionRunner.execute(() -> {
			agendaSwingView.getListStudentsModel().addElement(new Student("1", "test student"));
			agendaSwingView.getListStudentCoursesModel().addElement(new Course("1", "test course", "9"));
		});
		window.list("studentsList").selectItem(0);
		window.list("studentCoursesList").selectItem(0);
		window.button("removeCourseFromStudentButton").click();
		
		verify(agendaController).removeCourseFromStudent(new Student("1", "test student"), new Course("1", "test course", "9"));
	}
}
