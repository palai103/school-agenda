package controller;

import service.AgendaService;
import view.AgendaView;

public class AgendaController {
	
	private AgendaView agendaView;
	private AgendaService agendaService;

	public AgendaController() {}

	public void getAllStudents() {
		agendaView.showAllStudents(agendaService.getAllStudents());
	}	

}
