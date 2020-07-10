package controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import model.Student;
import service.AgendaService;
import view.AgendaView;

public class AgendaControllerTest {

	@Mock
	private AgendaView agendaView;

	@Mock
	private AgendaService agendaService;
	
	@InjectMocks
	private AgendaController agendaController;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetAllStudents() {
		// setup
		List<Student> allStudents = asList(new Student("1", "testStudent"));
		when(agendaService.getAllStudents()).thenReturn(allStudents);
		
		// exercise
		agendaController.getAllStudents();
		
		// verify
		verify(agendaView).showAllStudents(allStudents);
	}

}
