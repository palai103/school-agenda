package controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import model.Course;
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

	@Test
	public void testAddStudentWhenStudentIsNotPresentShouldAddAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		when(agendaService.findStudent(testStudent)).thenReturn(false);

		// exercise
		agendaController.addStudent(testStudent);

		// verify
		InOrder inOrder = inOrder(agendaView, agendaService);
		inOrder.verify(agendaService).addStudent(testStudent);
		inOrder.verify(agendaView).notifyStudentAdded(testStudent);
	}

	@Test
	public void testAddStudentWhenStudentIsAlreadyPresentShouldNotAddAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		when(agendaService.findStudent(testStudent)).thenReturn(true);

		// exercise
		agendaController.addStudent(testStudent);

		// verify
		verify(agendaView).notifyStudentNotAdded(testStudent);
		verifyNoMoreInteractions(ignoreStubs(agendaService));
	}

	@Test
	public void testRemoveStudentWhenStudentIsAlreadyPresentShouldRemoveAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		when(agendaService.findStudent(testStudent)).thenReturn(true);

		// exercise
		agendaController.removeStudent(testStudent);

		// verify
		InOrder inOrder = inOrder(agendaView, agendaService);
		inOrder.verify(agendaService).removeStudent(testStudent);
		inOrder.verify(agendaView).notifyStudentRemoved(testStudent);
	}

	@Test
	public void testRemoveStudentIsNotPresentShouldNotRemoveAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		when(agendaService.findStudent(testStudent)).thenReturn(false);

		// exercise
		agendaController.removeStudent(testStudent);

		// verify
		verify(agendaView).notifyStudentNotRemoved(testStudent);
		verifyNoMoreInteractions(ignoreStubs(agendaService));
	}

	/*Add course to student*/
	@Test
	public void testAddCourseToStudentShouldAddAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findStudent(testStudent)).thenReturn(true);
		when(agendaService.findCourse(testCourse)).thenReturn(true);
		when(agendaService.studentHasCourse(testStudent, testCourse)).thenReturn(false);

		// exercise
		agendaController.addCourseToStudent(testStudent, testCourse);

		// verify
		InOrder inOrder = inOrder(agendaService, agendaView);
		inOrder.verify(agendaService).addCourseToStudent(testStudent, testCourse);
		inOrder.verify(agendaView).notifyCourseAddedToStudent(testStudent, testCourse);
	}

	@Test
	public void testAddCourseToStudentWhenStudentIsNotPresentShouldNotAddAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findStudent(testStudent)).thenReturn(false);
		when(agendaService.findCourse(testCourse)).thenReturn(true);

		// exercise
		agendaController.addCourseToStudent(testStudent, testCourse);

		// verify
		verify(agendaView).notifyCourseNotAddedToStudent(testStudent, testCourse);
		verifyNoMoreInteractions(ignoreStubs(agendaService));
	}

	@Test
	public void testAddCourseToStudentWhenCourseIsNotPresentShouldNotAddAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findStudent(testStudent)).thenReturn(true);
		when(agendaService.findCourse(testCourse)).thenReturn(false);

		// exercise
		agendaController.addCourseToStudent(testStudent, testCourse);

		// verify
		verify(agendaView).notifyCourseNotAddedToStudent(testStudent, testCourse);
		verifyNoMoreInteractions(ignoreStubs(agendaService));
	}
	
	@Test
	public void testAddCourseToStudentWhenStudentHasItShouldNotAddAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findStudent(testStudent)).thenReturn(true);
		when(agendaService.findCourse(testCourse)).thenReturn(true);
		when(agendaService.studentHasCourse(testStudent, testCourse)).thenReturn(true);

		// exercise
		agendaController.addCourseToStudent(testStudent, testCourse);

		// verify
		verify(agendaView).notifyCourseNotAddedToStudent(testStudent, testCourse);
		verifyNoMoreInteractions(ignoreStubs(agendaService));
	}
	
	/*Remove course from student*/
	@Test
	public void testRemoveCourseFromStudentShouldRemoveAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findStudent(testStudent)).thenReturn(true);
		when(agendaService.findCourse(testCourse)).thenReturn(true);
		when(agendaService.studentHasCourse(testStudent, testCourse)).thenReturn(true);

		// exercise
		agendaController.removeCourseFromStudent(testStudent, testCourse);

		// verify
		InOrder inOrder = inOrder(agendaService, agendaView);
		inOrder.verify(agendaService).removeCourseFromStudent(testStudent, testCourse);
		inOrder.verify(agendaView).notifyCourseRemovedFromStudent(testStudent, testCourse);
	}
	
	@Test
	public void testRemoveCourseFromStudentWhenStudentIsNotPresentShouldNotAddAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findStudent(testStudent)).thenReturn(false);
		when(agendaService.findCourse(testCourse)).thenReturn(true);

		// exercise
		agendaController.removeCourseFromStudent(testStudent, testCourse);

		// verify
		verify(agendaView).notifyCourseNotRemovedFromStudent(testStudent, testCourse);
		verifyNoMoreInteractions(ignoreStubs(agendaService));
	}
	
	@Test
	public void testRemoveCourseFromStudentWhenCourseIsNotPresentShouldNotRemoveAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findStudent(testStudent)).thenReturn(true);
		when(agendaService.findCourse(testCourse)).thenReturn(false);

		// exercise
		agendaController.removeCourseFromStudent(testStudent, testCourse);

		// verify
		verify(agendaView).notifyCourseNotRemovedFromStudent(testStudent, testCourse);
		verifyNoMoreInteractions(ignoreStubs(agendaService));
	}
	
	@Test
	public void testRemoveCourseFromStudentWhenStudentNotHasItShouldNotRemoveAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findStudent(testStudent)).thenReturn(true);
		when(agendaService.findCourse(testCourse)).thenReturn(true);
		when(agendaService.studentHasCourse(testStudent, testCourse)).thenReturn(false);

		// exercise
		agendaController.removeCourseFromStudent(testStudent, testCourse);

		// verify
		verify(agendaView).notifyCourseNotRemovedFromStudent(testStudent, testCourse);
		verifyNoMoreInteractions(ignoreStubs(agendaService));
	}
	
	/*Add course*/
	@Test
	public void testAddCourseWhenCourseIsNotPresentShouldAddAndFeedback() {
		// setup
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findCourse(testCourse)).thenReturn(false);

		// exercise
		agendaController.addCourse(testCourse);

		// verify
		InOrder inOrder = inOrder(agendaService, agendaView);
		inOrder.verify(agendaService).addCourse(testCourse);
		inOrder.verify(agendaView).notifyCourseAdded(testCourse);
	}
	
	@Test
	public void testAddCourseWhenCourseIsAlreadyPresentShouldNotAddAndFeedback() {
		// setup
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findCourse(testCourse)).thenReturn(true);

		// exercise
		agendaController.addCourse(testCourse);

		// verify
		verify(agendaView).notifyCourseNotAdded(testCourse);
		verifyNoMoreInteractions(ignoreStubs(agendaService));
	}
	
	@Test
	public void testRemoveCourseWhenCourseIsAlreadyPresentShouldRemoveAndFeedback() {
		// setup
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findCourse(testCourse)).thenReturn(true);

		// exercise
		agendaController.removeCourse(testCourse);

		// verify
		InOrder inOrder = inOrder(agendaService, agendaView);
		inOrder.verify(agendaService).removeCourse(testCourse);
		inOrder.verify(agendaView).notifyCourseRemoved(testCourse);
	}
	
	@Test
	public void testRemoveCourseWhenCourseIsNotPresentShouldNotRemoveAndFeedback() {
		// setup
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findCourse(testCourse)).thenReturn(false);

		// exercise
		agendaController.removeCourse(testCourse);

		// verify
		verify(agendaView).notifyCourseNotRemoved(testCourse);
		verifyNoMoreInteractions(ignoreStubs(agendaService));
	}
	
	/*Remove student from course*/
	
	@Test
	public void testRemoveStudentFromCourseShouldRemoveAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findStudent(testStudent)).thenReturn(true);
		when(agendaService.findCourse(testCourse)).thenReturn(true);
		when(agendaService.courseHasStudent(testStudent, testCourse)).thenReturn(true);
		
		// exercise
		agendaController.removeStudentFromCourse(testStudent, testCourse);
		
		// verify
		InOrder inOrder = inOrder(agendaService, agendaView);
		inOrder.verify(agendaService).removeStudentFromCourse(testStudent, testCourse);
		inOrder.verify(agendaView).notifyStudentRemovedFromCourse(testStudent, testCourse);
	}
	
	@Test
	public void testRemoveStudentFromCourseWhenCourseNotHasItShouldNotRemoveAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findStudent(testStudent)).thenReturn(true);
		when(agendaService.findCourse(testCourse)).thenReturn(true);
		when(agendaService.courseHasStudent(testStudent, testCourse)).thenReturn(false);
		
		// exercise
		agendaController.removeStudentFromCourse(testStudent, testCourse);
		
		// verify
		verify(agendaView).notifyStudentNotRemovedFromCourse(testStudent, testCourse);
		verifyNoMoreInteractions(ignoreStubs(agendaService));
	}
	
	@Test
	public void testRemoveStudentFromCourseWhenStudentIsNotPresentShouldNotRemoveAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findStudent(testStudent)).thenReturn(false);
		when(agendaService.findCourse(testCourse)).thenReturn(true);
		
		// exercise
		agendaController.removeStudentFromCourse(testStudent, testCourse);
		
		// verify
		verify(agendaView).notifyStudentNotRemovedFromCourse(testStudent, testCourse);
	}
	
	@Test
	public void testRemoveStudentFromCourseWhenCourseIsNotPresentShouldNotRemoveAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findStudent(testStudent)).thenReturn(true);
		when(agendaService.findCourse(testCourse)).thenReturn(false);
		
		// exercise
		agendaController.removeStudentFromCourse(testStudent, testCourse);
		
		// verify
		verify(agendaView).notifyStudentNotRemovedFromCourse(testStudent, testCourse);
	}
	
	/*Add student to course*/
	
	@Test
	public void testAddStudentToCourseShouldAddAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findStudent(testStudent)).thenReturn(true);
		when(agendaService.findCourse(testCourse)).thenReturn(true);
		
		// exercise
		agendaController.addStudentToCourse(testStudent, testCourse);
		
		// verify
		InOrder inOrder = inOrder(agendaService, agendaView);
		inOrder.verify(agendaService).addStudentToCourse(testStudent, testCourse);
		inOrder.verify(agendaView).notifyStudentAddedToCourse(testStudent, testCourse);
	}
	
	@Test
	public void testAddStudentToCourseWhenCourseHasItShouldNotAddAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findStudent(testStudent)).thenReturn(true);
		when(agendaService.findCourse(testCourse)).thenReturn(true);
		when(agendaService.courseHasStudent(testStudent, testCourse)).thenReturn(true);
		
		// exercise
		agendaController.addStudentToCourse(testStudent, testCourse);
		
		// verify
		verify(agendaView).notifyStudentNotAddedToCourse(testStudent, testCourse);
		verifyNoMoreInteractions(ignoreStubs(agendaService));
	}
	
	@Test
	public void testAddStudentToCourseWhenStudentIsNotPresentShouldNotAddAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findStudent(testStudent)).thenReturn(false);
		when(agendaService.findCourse(testCourse)).thenReturn(true);
		
		// exercise
		agendaController.addStudentToCourse(testStudent, testCourse);
		
		// verify
		verify(agendaView).notifyStudentNotAddedToCourse(testStudent, testCourse);
		verifyNoMoreInteractions(ignoreStubs(agendaService));
	}
	
	@Test
	public void testAddStudentToCourseWhenCourseIsNotPresentShouldNotAddAndFeedback() {
		// setup
		Student testStudent = new Student("1", "testStudent");
		Course testCourse = new Course("1", "testCourse");
		when(agendaService.findStudent(testStudent)).thenReturn(true);
		when(agendaService.findCourse(testCourse)).thenReturn(false);
		
		// exercise
		agendaController.addStudentToCourse(testStudent, testCourse);
		
		// verify
		verify(agendaView).notifyStudentNotAddedToCourse(testStudent, testCourse);
		verifyNoMoreInteractions(ignoreStubs(agendaService));
	}
	
	

}
