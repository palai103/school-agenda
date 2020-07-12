package service;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import repository.CourseRepository;
import repository.StudentRepository;
import repository.TransactionManager;

public class AgendaServiceTest {
	
	private AgendaService agendaService;
	
	@Mock
	private TransactionManager transactionManager;
	
	@Mock
	private StudentRepository studentRepository;
	
	@Mock
	private CourseRepository courseRepository;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	

}
