package view;

import java.util.List;

import controller.AgendaController;
import model.Course;
import model.Student;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command
public class AgendaViewCli implements AgendaView, Runnable {

	AgendaController controller;

	@Spec
	CommandSpec spec;

	private <T> void printToOutput(T toPrint) {
		spec.commandLine().getOut().println(toPrint);
	}

	@Command(name = "getStudents")
	public void showAllStudentsCLI() {
		controller.getAllStudents();
	}

	@Override
	public void showAllStudents(List<Student> allStudents) {
		System.out.println(spec);
		printToOutput("Students:");
		for (Student student : allStudents) {
			printToOutput(student.toString());
		}
	}

	@Override
	public void notifyStudentAdded(Student student) {
		printToOutput("Added student:");
		printToOutput(student.toString());
	}

	@Override
	public void notifyStudentNotAdded(Student student) {
		printToOutput("Not added student:");
		printToOutput(student.toString());
	}

	@Override
	public void notifyStudentRemoved(Student student) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyStudentNotRemoved(Student student) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyCourseAddedToStudent(Student student, Course course) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyCourseNotAddedToStudent(Student student, Course course) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyCourseRemovedFromStudent(Student student, Course course) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyCourseNotRemovedFromStudent(Student student, Course course) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyCourseAdded(Course course) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyCourseNotAdded(Course course) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyCourseRemoved(Course course) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyCourseNotRemoved(Course course) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyStudentRemovedFromCourse(Student student, Course course) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyStudentNotRemovedFromCourse(Student student, Course course) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyStudentNotAddedToCourse(Student student, Course course) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyStudentAddedToCourse(Student student, Course course) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showAllCourses(List<Course> allCourses) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	public void inject(AgendaController controller) {
		this.controller = controller;
	}

}
