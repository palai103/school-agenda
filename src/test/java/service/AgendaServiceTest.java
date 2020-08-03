package service;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import model.Course;
import model.Student;
import repository.CourseRepository;
import repository.CourseTransactionCode;
import repository.StudentRepository;
import repository.StudentTransactionCode;
import repository.TransactionCode;
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

		when(transactionManager.studentTransaction(any()))
				.thenAnswer(answer((StudentTransactionCode<?> code) -> code.apply(studentRepository)));

		when(transactionManager.courseTransaction(any()))
				.thenAnswer(answer((CourseTransactionCode<?> code) -> code.apply(courseRepository)));

		when(transactionManager.compositeTransaction(any()))
				.thenAnswer(answer((TransactionCode<?> code) -> code.apply(studentRepository, courseRepository)));

		agendaService = new AgendaService(transactionManager);
	}

	/* Get all students */

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
		Course testCourse = new Course("1", "test course", "9");

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
		Course testCourse = new Course("1", "test course", "9");

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
		Course testCourse = new Course("1", "test course", "9");
		when(studentRepository.findStudentCourses(testStudent.getId()))
				.thenReturn(Collections.singletonList(testCourse.getId()));

		// exercise
		agendaService.removeStudent(testStudent);

		// verify
		InOrder inOrder = inOrder(transactionManager, studentRepository, courseRepository);
		inOrder.verify(transactionManager).compositeTransaction(any());
		inOrder.verify(studentRepository).findStudentCourses(testStudent.getId());
		inOrder.verify(courseRepository).removeCourseStudent(testStudent.getId(), testCourse.getId());
		inOrder.verify(studentRepository).delete(testStudent);
	}

	@Test
	public void testRemoveStudentWhenEmptyShouldNotRemove() {
		// setup
		Student testStudent = null;

		// exercise
		agendaService.removeStudent(testStudent);

		// verify
		verifyNoInteractions(studentRepository);
		verify(transactionManager).compositeTransaction(any());
	}

	@Test
	public void testGetAllCoursesWithNotEmptyListShouldReturnListWithAllCourses() {
		// setup
		Course firstCourse = new Course("1", "test course one", "9");
		Course secondCourse = new Course("2", "test course two", "9");

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
		Course testCourse = new Course("1", "test course", "9");

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
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent = new Student("1", "test student");
		when(courseRepository.findCourseStudents(testCourse.getId()))
				.thenReturn(Collections.singletonList(testStudent.getId()));

		// exercise
		agendaService.removeCourse(testCourse);

		// verify
		InOrder inOrder = inOrder(transactionManager, studentRepository, courseRepository);
		inOrder.verify(transactionManager).compositeTransaction(any());
		inOrder.verify(courseRepository).findCourseStudents(testCourse.getId());
		inOrder.verify(studentRepository).removeStudentCourse(testStudent.getId(), testCourse.getId());
		inOrder.verify(courseRepository).delete(testCourse);
	}

	@Test
	public void testRemoveCourseWhenEmptyShouldNotRemove() {
		// setup
		Course testCourse = null;

		// exercise
		agendaService.removeCourse(testCourse);

		// verify
		verifyNoInteractions(courseRepository);
		verify(transactionManager).compositeTransaction(any());
	}

	@Test
	public void testAddCourseToStudentWhenStudentExistsShouldAdd() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent = new Student("1", "test student");
		when(studentRepository.findById("1")).thenReturn(testStudent);

		// exercise
		agendaService.addCourseToStudent(testStudent, testCourse);

		// verify
		InOrder inOrder = inOrder(transactionManager, studentRepository, courseRepository);
		inOrder.verify(transactionManager).compositeTransaction(any());
		inOrder.verify(studentRepository).updateStudentCourses(testStudent.getId(), testCourse.getId());
		inOrder.verify(courseRepository).updateCourseStudents(testStudent.getId(), testCourse.getId());
	}

	@Test
	public void testAddCourseToStudentWhenStudentDoesNotExistShouldNotAdd() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent = new Student("1", "test student");
		when(studentRepository.findById("1")).thenReturn(null);

		// exercise
		agendaService.addCourseToStudent(testStudent, testCourse);

		// verify
		verify(studentRepository, never()).updateStudentCourses(testStudent.getId(), testCourse.getId());
		verify(courseRepository, never()).updateCourseStudents(testStudent.getId(), testCourse.getId());
		verify(transactionManager).compositeTransaction(any());
	}

	@Test
	public void testRemoveCourseFromStudentWhenStudentExistsShouldRemove() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent = new Student("1", "test student");
		when(studentRepository.findById("1")).thenReturn(testStudent);

		// exercise
		agendaService.removeCourseFromStudent(testStudent, testCourse);

		// verify
		InOrder inOrder = inOrder(transactionManager, studentRepository, courseRepository);
		inOrder.verify(transactionManager).compositeTransaction(any());
		inOrder.verify(studentRepository).removeStudentCourse(testStudent.getId(), testCourse.getId());
		inOrder.verify(courseRepository).removeCourseStudent(testStudent.getId(), testCourse.getId());
	}

	@Test
	public void testRemoveCourseFromSudentWhenStudentDoesNotExistShouldNotRemove() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent = new Student("1", "test student");
		when(studentRepository.findById("1")).thenReturn(null);

		// exercise
		agendaService.removeCourseFromStudent(testStudent, testCourse);

		// verify
		verify(studentRepository, never()).removeStudentCourse(testStudent.getId(), testCourse.getId());
		verify(courseRepository, never()).removeCourseStudent(testStudent.getId(), testCourse.getId());
		verify(transactionManager).compositeTransaction(any());
	}

	@Test
	public void testStudentHasCourseWhenSudentExistsAndHasItShouldReturnTrue() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
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
	public void testCourseHasStudentWhenCourseExistsAndHasItShouldReturnTrue() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent = new Student("1", "test student");

		List<String> courseStudents = asList(testStudent.getId());

		when(courseRepository.findById("1")).thenReturn(testCourse);
		when(courseRepository.findCourseStudents("1")).thenReturn(courseStudents);

		// exercise
		Boolean hasStudent = agendaService.courseHasStudent(testStudent, testCourse);

		// verify
		assertThat(hasStudent).isTrue();
		verify(transactionManager).courseTransaction(any());
	}

	@Test
	public void testCourseHasStudentsWhenCourseExistsAndDoesNotHaveItShouldReturnFalse() {
		// setup
		Student studentWithinList = new Student("1", "test student inside");
		Student studentOutsideList = new Student("2", "test student outside");
		Course testCourse = new Course("1", "test course", "9");

		List<String> courseStudents = asList(studentWithinList.getId());

		when(courseRepository.findById("1")).thenReturn(testCourse);
		when(courseRepository.findCourseStudents("1")).thenReturn(courseStudents);

		// exercise
		Boolean hasStudent = agendaService.courseHasStudent(studentOutsideList, testCourse);

		// verify
		assertThat(hasStudent).isFalse();
		verify(transactionManager).courseTransaction(any());
	}

	@Test
	public void testStudentHasCourseWhenStudentExistsAndDoesNotHaveItShouldReturnFalse() {
		// setup
		Course courseWithinList = new Course("1", "test course inside", "9");
		Course courseOutsideList = new Course("2", "test course outside", "9");
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
		Course testCourse = new Course("1", "test course", "9");
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
		Course testCourse = new Course("1", "test course", "9");
		when(courseRepository.findById("1")).thenReturn(testCourse);

		// exercise
		agendaService.addStudentToCourse(testStudent, testCourse);

		// verify
		InOrder inOrder = inOrder(transactionManager, studentRepository, courseRepository);
		inOrder.verify(transactionManager).compositeTransaction(any());
		inOrder.verify(courseRepository).updateCourseStudents(testStudent.getId(), testCourse.getId());
		inOrder.verify(studentRepository).updateStudentCourses(testStudent.getId(), testCourse.getId());
	}

	@Test
	public void testAddStudentToCourseWhenCourseDoesNotExistShouldNotAdd() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course", "9");
		when(courseRepository.findById("1")).thenReturn(null);

		// exercise
		agendaService.addStudentToCourse(testStudent, testCourse);

		// verify
		verify(courseRepository, never()).updateCourseStudents(testStudent.getId(), testCourse.getId());
		;
		verify(transactionManager).compositeTransaction(any());
	}

	@Test
	public void testRemoveStudentFromCourseWhenCourseExistsSouldRemove() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course", "9");
		when(courseRepository.findById("1")).thenReturn(testCourse);

		// exercise
		agendaService.removeStudentFromCourse(testStudent, testCourse);

		// verify
		InOrder inOrder = inOrder(transactionManager, studentRepository, courseRepository);
		inOrder.verify(transactionManager).compositeTransaction(any());
		inOrder.verify(courseRepository).removeCourseStudent(testStudent.getId(), testCourse.getId());
		inOrder.verify(studentRepository).removeStudentCourse(testStudent.getId(), testCourse.getId());
	}

	@Test
	public void testRemoveStudentFromCourseWhenCourseDoesNotExistShouldNotRemove() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course", "9");
		when(courseRepository.findById("1")).thenReturn(null);

		// exercise
		agendaService.removeStudentFromCourse(testStudent, testCourse);

		// verify
		verify(courseRepository, never()).removeCourseStudent(testStudent.getId(), testCourse.getId());
		verify(transactionManager).compositeTransaction(any());
	}
}
