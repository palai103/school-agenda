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
		return;
	}

	public void addStudent(Student student) {
		if (Boolean.FALSE.equals(agendaService.findStudent(student))) {
			agendaService.addStudent(student);

			agendaView.notifyStudentAdded(student);
			return;
		}
		else {
			agendaView.notifyStudentNotAdded(student);
			return;
		}
	}

	public void removeStudent(Student student) {
		if (Boolean.TRUE.equals(agendaService.findStudent(student))) {
			agendaService.removeStudent(student);

			agendaView.notifyStudentRemoved(student);
			return;
		}
		else {
			agendaView.notifyStudentNotRemoved(student);
			return;
		}
	}

	public void addCourseToStudent(Student student, Course course) {
		if (Boolean.TRUE.equals(agendaService.findStudent(student)) &&
				Boolean.TRUE.equals(agendaService.findCourse(course))) {

			if (Boolean.TRUE.equals(agendaService.studentHasCourse(student, course))) {
				agendaView.notifyCourseNotAddedToStudent(student, course);
				return;
			}
			else {
				agendaService.addCourseToStudent(student, course);

				agendaView.notifyCourseAddedToStudent(student, course);
				return;
			}
		}

		if (Boolean.FALSE.equals(agendaService.findStudent(student)) &&
				Boolean.TRUE.equals(agendaService.findCourse(course))) {
			agendaView.notifyCourseNotAddedToStudent(student, course);
			return;
		}

		if (Boolean.TRUE.equals(agendaService.findStudent(student)) &&
				Boolean.FALSE.equals(agendaService.findCourse(course))) {
			agendaView.notifyCourseNotAddedToStudent(student, course);
			return;
		}
	}

	public void removeCourseFromStudent(Student student, Course course) {
		if (Boolean.TRUE.equals(agendaService.findStudent(student)) &&
				Boolean.TRUE.equals(agendaService.findCourse(course))) {

			if (Boolean.FALSE.equals(agendaService.studentHasCourse(student, course))) {
				agendaView.notifyCourseNotRemovedFromStudent(student, course);
				return;
			}
			else {
				agendaService.removeCourseFromStudent(student, course);

				agendaView.notifyCourseRemovedFromStudent(student, course);
				return;
			}
		}

		if (Boolean.FALSE.equals(agendaService.findStudent(student)) &&
				Boolean.TRUE.equals(agendaService.findCourse(course))) {
			agendaView.notifyCourseNotRemovedFromStudent(student, course);
			return;
		}

		if (Boolean.TRUE.equals(agendaService.findStudent(student)) &&
				Boolean.FALSE.equals(agendaService.findCourse(course))) {
			agendaView.notifyCourseNotRemovedFromStudent(student, course);
			return;
		}

	}	

}
