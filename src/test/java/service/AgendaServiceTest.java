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

import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;

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
	private MongoClient client;

	@Mock
	private TransactionManager transactionManager;

	@Mock
	private StudentRepository studentRepository;

	@Mock
	private CourseRepository courseRepository;

	private ClientSession clientSession;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		clientSession = client.startSession();

		when(transactionManager.studentTransaction(any()))
				.thenAnswer(answer((StudentTransactionCode<?> code) -> code.apply(studentRepository, clientSession)));

		when(transactionManager.courseTransaction(any()))
				.thenAnswer(answer((CourseTransactionCode<?> code) -> code.apply(courseRepository, clientSession)));

		when(transactionManager.compositeTransaction(any())).thenAnswer(
				answer((TransactionCode<?> code) -> code.apply(studentRepository, courseRepository, clientSession)));

		agendaService = new AgendaService(transactionManager);
	}

	/* Get all students */

	@Test
	public void testGetAllStudentsWithNotEmptyListShouldReturnListWithAllStudents() {
		// setup
		Student firstStudent = new Student("1", "test student one");
		Student secondStudent = new Student("2", "test student two");

		List<Student> allStudents = asList(firstStudent, secondStudent);
		when(studentRepository.findAll(clientSession)).thenReturn(allStudents);

		// exercise
		List<Student> retrievedStudents = agendaService.getAllStudents();

		// verify
		assertThat(retrievedStudents).containsExactly(firstStudent, secondStudent);
		verify(transactionManager).studentTransaction(any());
	}

	@Test
	public void testGetAllStudentsWithEmptyListShouldReturnEmptyList() {
		// setup
		when(studentRepository.findAll(clientSession)).thenReturn(Collections.emptyList());

		// exercise
		List<Student> retrievedStudents = agendaService.getAllStudents();

		// verify
		assertThat(retrievedStudents).isEmpty();
		verify(transactionManager).studentTransaction(any());
	}

	@Test
	public void testGetAllStudentCoursesWithNotEmptyListShouldReturnListWithAllStudentCourses() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse1 = new Course("1", "student test course 1", "9");
		Course testCourse2 = new Course("2", "student test course 2", "9");
		when(studentRepository.findStudentCourses(clientSession, testStudent.getId()))
				.thenReturn(asList(testCourse1, testCourse2));

		// exercise
		List<Course> retrievedStudentCourses = agendaService.getAllStudentCourses(testStudent);

		// verify
		assertThat(retrievedStudentCourses).containsExactly(testCourse1, testCourse2);
		verify(transactionManager).studentTransaction(any());
	}

	@Test
	public void tetsGetAllStudentCoursesWhenStudentHasNoCoursesShouldReturnEmptyList() {
		// setup
		Student testStudent = new Student("1", "test student");
		when(studentRepository.findStudentCourses(clientSession, testStudent.getId()))
				.thenReturn(Collections.emptyList());

		// exercise
		List<Course> retirevedStudentCourses = agendaService.getAllStudentCourses(testStudent);

		// verify
		assertThat(retirevedStudentCourses).isEmpty();
		verify(transactionManager).studentTransaction(any());
	}

	@Test
	public void testFindStudentWhenExistsShouldReturnTrue() {
		// setup
		Student testStudent = new Student("1", "test student");

		when(studentRepository.findById(clientSession, "1")).thenReturn(testStudent);

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

		when(studentRepository.findById(clientSession, "1")).thenReturn(null);

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

		when(courseRepository.findById(clientSession, "1")).thenReturn(testCourse);

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

		when(courseRepository.findById(clientSession, "1")).thenReturn(null);

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
		verify(studentRepository).save(clientSession, testStudent);
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
		when(studentRepository.findStudentCourses(clientSession, testStudent.getId()))
				.thenReturn(Collections.singletonList(testCourse));

		// exercise
		agendaService.removeStudent(testStudent);

		// verify
		InOrder inOrder = inOrder(transactionManager, studentRepository, courseRepository);
		inOrder.verify(transactionManager).compositeTransaction(any());
		inOrder.verify(studentRepository).findStudentCourses(clientSession, testStudent.getId());
		inOrder.verify(courseRepository).removeCourseStudent(clientSession, testStudent.getId(), testCourse.getId());
		inOrder.verify(studentRepository).delete(clientSession, testStudent);
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
		when(courseRepository.findAll(clientSession)).thenReturn(allCourses);

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
		when(courseRepository.findAll(clientSession)).thenReturn(allCourses);

		// exercise
		List<Course> retrievedCourses = agendaService.getAllCourses();

		// verify
		assertThat(retrievedCourses).isEmpty();
		verify(transactionManager).courseTransaction(any());
	}
	
	@Test
	public void testGetAllCourseStudentsWithNotEmptyListShouldReturnListWithAllCourseStudents() {
		// setup
		Course testCourse = new Course("1", "student test course 1", "9");
		Student testStudent1 = new Student("1", "test student 1");
		Student testStudent2 = new Student("2", "test student 2");
		when(courseRepository.findCourseStudents(clientSession, testCourse.getId()))
				.thenReturn(asList(testStudent1, testStudent2));

		// exercise
		List<Student> retrievedCourseStudents = agendaService.getAllCourseStudents(testCourse);

		// verify
		assertThat(retrievedCourseStudents).containsExactly(testStudent1, testStudent2);
		verify(transactionManager).courseTransaction(any());
	}

	@Test
	public void ttestGetAllCourseStudentsWhenCourseHasNoStudentsShouldReturnEmptyList() {
		// setup
		Course testCourse = new Course("1", "student test course 1", "9");
		when(courseRepository.findCourseStudents(clientSession, testCourse.getId()))
				.thenReturn(Collections.emptyList());

		// exercise
		List<Student> retrievedCourseStudents = agendaService.getAllCourseStudents(testCourse);

		// verify
		assertThat(retrievedCourseStudents).isEmpty();
		verify(transactionManager).courseTransaction(any());
	}

	@Test
	public void testAddCourseWhenNotEmptyShouldAdd() {
		// setup
		Course testCourse = new Course("1", "test course", "9");

		// exercise
		agendaService.addCourse(testCourse);

		// verify
		verify(courseRepository).save(clientSession, testCourse);
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
		when(courseRepository.findCourseStudents(clientSession, testCourse.getId()))
				.thenReturn(Collections.singletonList(testStudent));

		// exercise
		agendaService.removeCourse(testCourse);

		// verify
		InOrder inOrder = inOrder(transactionManager, studentRepository, courseRepository);
		inOrder.verify(transactionManager).compositeTransaction(any());
		inOrder.verify(courseRepository).findCourseStudents(clientSession, testCourse.getId());
		inOrder.verify(studentRepository).removeStudentCourse(clientSession, testStudent.getId(), testCourse.getId());
		inOrder.verify(courseRepository).delete(clientSession, testCourse);
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
		when(studentRepository.findById(clientSession, "1")).thenReturn(testStudent);

		// exercise
		agendaService.addCourseToStudent(testStudent, testCourse);

		// verify
		InOrder inOrder = inOrder(transactionManager, studentRepository, courseRepository);
		inOrder.verify(transactionManager).compositeTransaction(any());
		inOrder.verify(studentRepository).updateStudentCourses(clientSession, testStudent.getId(), testCourse.getId());
		inOrder.verify(courseRepository).updateCourseStudents(clientSession, testStudent.getId(), testCourse.getId());
	}

	@Test
	public void testAddCourseToStudentWhenStudentDoesNotExistShouldNotAdd() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent = new Student("1", "test student");
		when(studentRepository.findById(clientSession, "1")).thenReturn(null);

		// exercise
		agendaService.addCourseToStudent(testStudent, testCourse);

		// verify
		verify(studentRepository, never()).updateStudentCourses(clientSession, testStudent.getId(), testCourse.getId());
		verify(courseRepository, never()).updateCourseStudents(clientSession, testStudent.getId(), testCourse.getId());
		verify(transactionManager).compositeTransaction(any());
	}

	@Test
	public void testRemoveCourseFromStudentWhenStudentExistsShouldRemove() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent = new Student("1", "test student");
		when(studentRepository.findById(clientSession, "1")).thenReturn(testStudent);

		// exercise
		agendaService.removeCourseFromStudent(testStudent, testCourse);

		// verify
		InOrder inOrder = inOrder(transactionManager, studentRepository, courseRepository);
		inOrder.verify(transactionManager).compositeTransaction(any());
		inOrder.verify(studentRepository).removeStudentCourse(clientSession, testStudent.getId(), testCourse.getId());
		inOrder.verify(courseRepository).removeCourseStudent(clientSession, testStudent.getId(), testCourse.getId());
	}

	@Test
	public void testRemoveCourseFromSudentWhenStudentDoesNotExistShouldNotRemove() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent = new Student("1", "test student");
		when(studentRepository.findById(clientSession, "1")).thenReturn(null);

		// exercise
		agendaService.removeCourseFromStudent(testStudent, testCourse);

		// verify
		verify(studentRepository, never()).removeStudentCourse(clientSession, testStudent.getId(), testCourse.getId());
		verify(courseRepository, never()).removeCourseStudent(clientSession, testStudent.getId(), testCourse.getId());
		verify(transactionManager).compositeTransaction(any());
	}

	@Test
	public void testStudentHasCourseWhenSudentExistsAndHasItShouldReturnTrue() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent = new Student("1", "test student");

		List<Course> studentCourses = asList(testCourse);

		when(studentRepository.findById(clientSession, "1")).thenReturn(testStudent);
		when(studentRepository.findStudentCourses(clientSession, "1")).thenReturn(studentCourses);

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

		List<Student> courseStudents = asList(testStudent);

		when(courseRepository.findById(clientSession, "1")).thenReturn(testCourse);
		when(courseRepository.findCourseStudents(clientSession, "1")).thenReturn(courseStudents);

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

		List<Student> courseStudents = asList(studentWithinList);

		when(courseRepository.findById(clientSession, "1")).thenReturn(testCourse);
		when(courseRepository.findCourseStudents(clientSession, "1")).thenReturn(courseStudents);

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

		List<Course> studentCourses = asList(courseWithinList);

		when(studentRepository.findById(clientSession, "1")).thenReturn(testStudent);
		when(studentRepository.findStudentCourses(clientSession, "1")).thenReturn(studentCourses);

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
		when(studentRepository.findById(clientSession, "1")).thenReturn(null);

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
		when(courseRepository.findById(clientSession, "1")).thenReturn(testCourse);

		// exercise
		agendaService.addStudentToCourse(testStudent, testCourse);

		// verify
		InOrder inOrder = inOrder(transactionManager, studentRepository, courseRepository);
		inOrder.verify(transactionManager).compositeTransaction(any());
		inOrder.verify(courseRepository).updateCourseStudents(clientSession, testStudent.getId(), testCourse.getId());
		inOrder.verify(studentRepository).updateStudentCourses(clientSession, testStudent.getId(), testCourse.getId());
	}

	@Test
	public void testAddStudentToCourseWhenCourseDoesNotExistShouldNotAdd() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course", "9");
		when(courseRepository.findById(clientSession, "1")).thenReturn(null);

		// exercise
		agendaService.addStudentToCourse(testStudent, testCourse);

		// verify
		verify(courseRepository, never()).updateCourseStudents(clientSession, testStudent.getId(), testCourse.getId());
		;
		verify(transactionManager).compositeTransaction(any());
	}

	@Test
	public void testRemoveStudentFromCourseWhenCourseExistsSouldRemove() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course", "9");
		when(courseRepository.findById(clientSession, "1")).thenReturn(testCourse);

		// exercise
		agendaService.removeStudentFromCourse(testStudent, testCourse);

		// verify
		InOrder inOrder = inOrder(transactionManager, studentRepository, courseRepository);
		inOrder.verify(transactionManager).compositeTransaction(any());
		inOrder.verify(courseRepository).removeCourseStudent(clientSession, testStudent.getId(), testCourse.getId());
		inOrder.verify(studentRepository).removeStudentCourse(clientSession, testStudent.getId(), testCourse.getId());
	}

	@Test
	public void testRemoveStudentFromCourseWhenCourseDoesNotExistShouldNotRemove() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course", "9");
		when(courseRepository.findById(clientSession, "1")).thenReturn(null);

		// exercise
		agendaService.removeStudentFromCourse(testStudent, testCourse);

		// verify
		verify(courseRepository, never()).removeCourseStudent(clientSession, testStudent.getId(), testCourse.getId());
		verify(transactionManager).compositeTransaction(any());
	}
}
