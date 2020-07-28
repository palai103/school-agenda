package view;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import controller.AgendaController;
import model.Student;
import picocli.CommandLine;
import service.AgendaService;

public class AgendaViewTest {

	private CommandLine cmd;

	@Mock
	private AgendaService service;

	@InjectMocks
	private AgendaController controller;

	@InjectMocks
	private AgendaViewCli cliView;

	private StringWriter sw;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		cliView = new AgendaViewCli();
		cliView.inject(controller);
		controller = new AgendaController(cliView, service);
		cmd = new CommandLine(cliView);
		sw = new StringWriter();
		cmd.setOut(new PrintWriter(sw));
	}

	@Test
	public void testShowAllStudents() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		List<Student> allStudents = asList(testStudent);
		when(service.getAllStudents()).thenReturn(allStudents);

		// exercise
		cmd.execute("getStudents");

		// verify
		assertThat("Students:\n" + "Student [id=1, name=testStudent]\n").isEqualTo(sw.toString());
	}
}
