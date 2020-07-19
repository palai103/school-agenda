package service;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.never;
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

	@Test
	public void testAddStudentWhenNotEmptyShouldAdd() {
		// setup
		Student testStudent = new Student("1", "test student");

		// exercise
		agendaService.addStudent(testStudent);

		// verify
		verify(studentRepository).save(testStudent);
		verify(transactionManager).studentTransaction(any());
	}

	@Test
	public void testAddStudentWhenEmptyShouldNotAdd() {
		// setup
		Student testStudent = null;

		// exercise
		agendaService.addStudent(testStudent);

		// verify
		verifyNoInteractions(studentRepository);
		verify(transactionManager).studentTransaction(any());
	}

	@Test
	public void testRemoveStudentWhenNotEmptyShouldRemove() {
		// setup
		Student testStudent = new Student("1", "test student");

		// exercise
		agendaService.removeStudent(testStudent);

		// verify
		verify(studentRepository).delete(testStudent);
		verify(transactionManager).studentTransaction(any());
	}

	@Test
	public void testRemoveStudentWhenEmptyShouldNotRemove() {
		// setup
		Student testStudent = null;

		// exercise
		agendaService.removeStudent(testStudent);

		// verify
		verifyNoInteractions(studentRepository);
		verify(transactionManager).studentTransaction(any());
	}

	@Test
	public void testGetAllCoursesWithNotEmptyListShouldReturnListWithAllCourses() {
		// setup
		Course firstCourse = new Course("1", "test course one");
		Course secondCourse = new Course("2", "test course two");

		List<Course> allCourses = asList(firstCourse, secondCourse);
		when(courseRepository.findAll()).thenReturn(allCourses);

		// exercise
		List<Course> retrievedCourses = agendaService.getAllCourses();

		// verify
		assertThat(retrievedCourses).containsExactly(firstCourse, secondCourse);
		verify(transactionManager).courseTransaction(any());
	}

	@Test
	public void testGetAllCoursesWithEmptyListShouldReturnEmptyList() {
		// setup
		List<Course> allCourses = asList();
		when(courseRepository.findAll()).thenReturn(allCourses);

		// exercise
		List<Course> retrievedCourses = agendaService.getAllCourses();

		// verify
		assertThat(retrievedCourses).isEmpty();
		verify(transactionManager).courseTransaction(any());
	}

	@Test
	public void testAddCourseWhenNotEmptyShouldAdd() {
		// setup
		Course testCourse = new Course("1", "test course");

		// exercise
		agendaService.addCourse(testCourse);

		// verify
		verify(courseRepository).save(testCourse);
		verify(transactionManager).courseTransaction(any());
	}

	@Test
	public void testAddCourseWhenEmptyShouldNotAdd() {
		// setup
		Course testCourse = null;

		// exercise
		agendaService.addCourse(testCourse);

		// verify
		verifyNoInteractions(courseRepository);
		verify(transactionManager).courseTransaction(any());
	}

	@Test
	public void testRemoveCourseWhenNotEmptyShouldRemove() {
		// setup
		Course testCourse = new Course("1", "test course");

		// exercise
		agendaService.removeCourse(testCourse);

		// verify
		verify(courseRepository).delete(testCourse);
		verify(transactionManager).courseTransaction(any());
	}

	@Test
	public void testRemoveCourseWhenEmptyShouldNotRemove() {
		// setup
		Course testCourse = null;

		// exercise
		agendaService.removeCourse(testCourse);

		// verify
		verifyNoInteractions(courseRepository);
		verify(transactionManager).courseTransaction(any());
	}

	@Test
	public void testAddCourseToStudentWhenStudentExistsShouldAdd() {
		// setup
		Course testCourse = new Course("1", "test course");
		Student testStudent = new Student("1", "test student");
		when(studentRepository.findById("1")).thenReturn(testStudent);

		// exercise
		agendaService.addCourseToStudent(testStudent, testCourse);

		// verify
		verify(studentRepository).updateStudentCourses(testStudent.getId(), testCourse.getId());
		verify(transactionManager).studentTransaction(any());
	}

	@Test
	public void testAddCourseToStudentWhenStudentDoesNotExistShouldNotAdd() {
		// setup
		Course testCourse = new Course("1", "test course");
		Student testStudent = new Student("1", "test student");
		when(studentRepository.findById("1")).thenReturn(null);

		// exercise
		agendaService.addCourseToStudent(testStudent, testCourse);

		// verify
		verify(studentRepository, never()).updateStudentCourses(testStudent.getId(), testCourse.getId());
		verify(transactionManager).studentTransaction(any());
	}

	@Test
	public void testRemoveCourseFromStudentWhenStudentExistsSouldRemove() {
		// setup
		Course testCourse = new Course("1", "test course");
		Student testStudent = new Student("1", "test student");
		when(studentRepository.findById("1")).thenReturn(testStudent);

		// exercise
		agendaService.removeCourseFromStudent(testStudent, testCourse);

		// verify
		verify(studentRepository).removeStudentCourse(testStudent.getId(), testCourse.getId());
		verify(transactionManager).studentTransaction(any());
	}

	@Test
	public void testRemoveCourseFromSudentWhenStudentDoesNotExistShouldNotRemove() {
		// setup
		Course testCourse = new Course("1", "test course");
		Student testStudent = new Student("1", "test student");
		when(studentRepository.findById("1")).thenReturn(null);

		// exercise
		agendaService.removeCourseFromStudent(testStudent, testCourse);

		// verify
		verify(studentRepository, never()).removeStudentCourse(testStudent.getId(), testCourse.getId());
		verify(transactionManager).studentTransaction(any());
	}

	@Test
	public void testStudentHasCourseWhenSudentExistsAndHasItShouldReturnTrue() {
		// setup
		Course testCourse = new Course("1", "test course");
		Student testStudent = new Student("1", "test student");

		List<String> studentCourses = asList(testCourse.getId());

		when(studentRepository.findById("1")).thenReturn(testStudent);
		when(studentRepository.findStudentCourses("1")).thenReturn(studentCourses);

		// exercise
		Boolean hasCourse = agendaService.studentHasCourse(testStudent, testCourse);

		// verify
		assertThat(hasCourse).isTrue();
		verify(transactionManager).studentTransaction(any());
	}

	@Test
	public void testStudentHasCourseWhenStudentExistsAndDoesNotHaveItShouldReturnFalse() {
		// setup
		Course courseWithinList = new Course("1", "test course inside");
		Course courseOutsideList = new Course("2", "test course outside");
		Student testStudent = new Student("1", "test student");

		List<String> studentCourses = asList(courseWithinList.getId());

		when(studentRepository.findById("1")).thenReturn(testStudent);
		when(studentRepository.findStudentCourses("1")).thenReturn(studentCourses);

		// exercise
		Boolean hasCourse = agendaService.studentHasCourse(testStudent, courseOutsideList);

		// verify
		assertThat(hasCourse).isFalse();
		verify(transactionManager).studentTransaction(any());
	}

	@Test
	public void testStudentHasCourseWhenStudentDoesNotExistShouldReturnFalse() {
		// setup
		Course testCourse = new Course("1", "test course");
		Student testStudent = new Student("1", "test student");
		when(studentRepository.findById("1")).thenReturn(null);

		// exercise
		Boolean hasCourse = agendaService.studentHasCourse(testStudent, testCourse);

		// verify
		assertThat(hasCourse).isFalse();
		verify(transactionManager).studentTransaction(any());
	}

	@Test
	public void testAddStudentToCourseWhenCourseExistsShouldAdd() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course");
		when(courseRepository.findById("1")).thenReturn(testCourse);

		// exercise
		agendaService.addStudentToCourse(testStudent, testCourse);

		// verify
		verify(courseRepository).updateCourseStudents(testStudent.getId(), testCourse.getId());
		verify(transactionManager).courseTransaction(any());
	}

	@Test
	public void testAddStudentToCourseWhenCourseDoesNotExistShouldNotAdd() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course");
		when(courseRepository.findById("1")).thenReturn(null);

		// exercise
		agendaService.addStudentToCourse(testStudent, testCourse);

		// verify
		verify(courseRepository, never()).updateCourseStudents(testStudent.getId(), testCourse.getId());;
		verify(transactionManager).courseTransaction(any());
	}

	@Test
	public void testRemoveStudentFromCourseWhenCourseExistsSouldRemove() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course");
		when(courseRepository.findById("1")).thenReturn(testCourse);

		// exercise
		agendaService.removeStudentFromCourse(testStudent, testCourse);

		// verify
		verify(courseRepository).removeCourseStudent(testStudent.getId(), testCourse.getId());
		verify(transactionManager).courseTransaction(any());
	}

	@Test
	public void testRemoveStudentFromCourseWhenCourseDoesNotExistShouldNotRemove() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course");
		when(courseRepository.findById("1")).thenReturn(null);

		// exercise
		agendaService.removeStudentFromCourse(testStudent, testCourse);

		// verify
		verify(courseRepository, never()).removeCourseStudent(testStudent.getId(), testCourse.getId());
		verify(transactionManager).courseTransaction(any());
	}
	
}
