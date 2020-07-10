package controller;

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
		if (!agendaService.findStudent(student)) {
			agendaService.addStudent(student);
			
			agendaView.notifyStudentAdded(student);
		}
	}	

}
