package controller;

import model.Course;
import model.Student;
import service.AgendaService;
import view.AgendaView;

public class AgendaController {

	private AgendaView agendaView;
	private AgendaService agendaService;

	public void getAllStudents() {
		agendaView.showAllStudents(agendaService.getAllStudents());
	}

	public void addStudent(Student student) {
		if (Boolean.FALSE.equals(agendaService.findStudent(student))) {
			agendaService.addStudent(student);
			agendaView.notifyStudentAdded(student);
		}
		else {
			agendaView.notifyStudentNotAdded(student);
		}
	}

	public void removeStudent(Student student) {
		if (Boolean.TRUE.equals(agendaService.findStudent(student))) {
			agendaService.removeStudent(student);
			agendaView.notifyStudentRemoved(student);
		}
		else {
			agendaView.notifyStudentNotRemoved(student);
		}
	}

	public void addCourseToStudent(Student student, Course course) {
		if (Boolean.TRUE.equals(agendaService.findStudent(student)) &&
				Boolean.TRUE.equals(agendaService.findCourse(course))) {

			if (Boolean.TRUE.equals(agendaService.studentHasCourse(student, course))) {
				agendaView.notifyCourseNotAddedToStudent(student, course);
			}
			else {
				agendaService.addCourseToStudent(student, course);
				agendaView.notifyCourseAddedToStudent(student, course);
			}
		}
		else {
			agendaView.notifyCourseNotAddedToStudent(student, course);
		}
	}

	public void removeCourseFromStudent(Student student, Course course) {
		if (Boolean.TRUE.equals(agendaService.findStudent(student)) &&
				Boolean.TRUE.equals(agendaService.findCourse(course))) {

			if (Boolean.FALSE.equals(agendaService.studentHasCourse(student, course))) {
				agendaView.notifyCourseNotRemovedFromStudent(student, course);
			}
			else {
				agendaService.removeCourseFromStudent(student, course);
				agendaView.notifyCourseRemovedFromStudent(student, course);
			}
		}
		else {
			agendaView.notifyCourseNotRemovedFromStudent(student, course);
		}
	}

	public void addCourse(Course course) {
		if(Boolean.FALSE.equals(agendaService.findCourse(course))) {
			agendaService.addCourse(course);
			agendaView.notifyCourseAdded(course);
		}
		else {
			agendaView.notifyCourseNotAdded(course);
		}
	}

	public void removeCourse(Course course) {
		if(Boolean.TRUE.equals(agendaService.findCourse(course))) {
			agendaService.removeCourse(course);
			agendaView.notifyCourseRemoved(course);
		}
		else {
			agendaView.notifyCourseNotRemoved(course);
		}
	}

	public void removeStudentFromCourse(Student student, Course course) {
		if(Boolean.TRUE.equals(agendaService.findStudent(student)) &&
				Boolean.TRUE.equals(agendaService.findCourse(course))) {

			if(Boolean.TRUE.equals(agendaService.courseHasStudent(student, course))) {
				agendaService.removeStudentFromCourse(student, course);
				agendaView.notifyStudentRemovedFromCourse(student, course);
			} 
			else {
				agendaView.notifyStudentNotRemovedFromCourse(student, course);
			}
		}
		else {
			agendaView.notifyStudentNotRemovedFromCourse(student, course);
		}	
	}

	public void addStudentToCourse(Student student, Course course) {
		if(Boolean.TRUE.equals(agendaService.findStudent(student)) &&
				Boolean.TRUE.equals(agendaService.findCourse(course))) {
			
			if(Boolean.FALSE.equals(agendaService.courseHasStudent(student, course))) {
				agendaService.addStudentToCourse(student, course);
				agendaView.notifyStudentAddedToCourse(student, course);
			} else {
				agendaView.notifyStudentNotAddedToCourse(student, course);
			}
		} else {
			agendaView.notifyStudentNotAddedToCourse(student, course);
		}

	}

	public void getAllCourses() {
		agendaView.showAllCourses(agendaService.getAllCourses());
		
	}	

}
