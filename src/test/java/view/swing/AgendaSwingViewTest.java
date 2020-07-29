package view.swing;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import controller.AgendaController;

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
		window.label("studentInfoErrorMessageLabel");
		window.label("coursePanel");
		window.label("courseIDLabel");
		window.label("courseNameLabel");
		window.label("courseCFULabel");
		window.label("courseInfoErrorMessageLabel");
		
		// Text fields check
		window.textBox("studentIDTextField").requireEnabled();
		window.textBox("studentNameTextField").requireEnabled();
		window.textBox("courseIDTextField").requireEnabled();
		window.textBox("courseNameTextField").requireEnabled();
		window.textBox("courseCFUTextField").requireEnabled();
		
		// Buttons check
		window.button("addNewStudentButton").requireDisabled();
		window.button("removeStudentButton").requireDisabled();
		window.button("addNewCourseButton").requireDisabled();
		window.button("removeCourseButton").requireDisabled();
		
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
}
