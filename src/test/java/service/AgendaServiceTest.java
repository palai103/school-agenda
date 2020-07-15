package service;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import model.Course;
import model.Student;
import repository.CourseRepository;
import repository.CourseTransactionCode;
import repository.StudentRepository;
import repository.StudentTransactionCode;
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
		
		when(transactionManager.studentTransaction(any())).thenAnswer(
				answer((StudentTransactionCode<?> code) -> code.apply(studentRepository)));
		
		when(transactionManager.courseTransaction(any())).thenAnswer(
				answer((CourseTransactionCode<?> code) -> code.apply(courseRepository)));
		
		agendaService = new AgendaService(transactionManager);
	}
	
	/*Get all students*/
	
	@Test
	public void testGetAllStudentsWithNotEmptyListShouldReturnListWithAllStudents() {
		// setup
		Student firstStudent = new Student("1", "test student one");
		Student secondStudent = new Student("2", "test student two");
		
		List<Student> allStudents = asList(firstStudent, secondStudent);
		when(studentRepository.findAll()).thenReturn(allStudents);

		// exercise
		List<Student> retrievedStudents = agendaService.getAllStudents();

		// verify
		assertThat(retrievedStudents).containsExactly(firstStudent, secondStudent);
		verify(transactionManager).studentTransaction(any());
	}
	
	@Test
	public void testGetAllStudentsWithEmptyListShouldReturnEmptyList() {
		// setup
		List<Student> allStudents = asList();
		when(studentRepository.findAll()).thenReturn(allStudents);

		// exercise
		List<Student> retrievedStudents = agendaService.getAllStudents();

		// verify
		assertThat(retrievedStudents).isEmpty();
		verify(transactionManager).studentTransaction(any());
	}
	
	@Test
	public void testFindStudentWhenExistsShouldReturnTrue() {
		// setup
		Student testStudent = new Student("1", "test student");
		
		when(studentRepository.findById("1")).thenReturn(testStudent);

		// exercise
		Boolean studentExists = agendaService.findStudent(testStudent);

		// verify
		assertThat(studentExists).isTrue();
		verify(transactionManager).studentTransaction(any());
	}
	
	@Test
	public void testFindStudentWhenDoesNotExistShouldReturnFalse() {
		// setup
		Student testStudent = new Student("1", "test student");
		
		when(studentRepository.findById("1")).thenReturn(null);

		// exercise
		Boolean studentNotExists = agendaService.findStudent(testStudent);

		// verify
		assertThat(studentNotExists).isFalse();
		verify(transactionManager).studentTransaction(any());
	}
	
	@Test
	public void testFindCourseWhenExistsShouldReturnTrue() {
		// setup
		Course testCourse = new Course("1", "test course");
		
		when(courseRepository.findById("1")).thenReturn(testCourse);

		// exercise
		Boolean courseExists = agendaService.findCourse(testCourse);

		// verify
		assertThat(courseExists).isTrue();
		verify(transactionManager).courseTransaction(any());
	}
	
	@Test
	public void testFindCourseWhenDoesNotExistShouldReturnFalse() {
		// setup
		Course testCourse = new Course("1", "test course");
		
		when(courseRepository.findById("1")).thenReturn(null);

		// exercise
		Boolean courseNotExists = agendaService.findCourse(testCourse);

		// verify
		assertThat(courseNotExists).isFalse();
		verify(transactionManager).courseTransaction(any());
	}
	
}
