package view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Frame;
import java.net.InetSocketAddress;
import java.util.Collections;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.driver.FrameDriver;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.AbstractContainerFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.fixture.JTabbedPaneFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import controller.AgendaController;
import model.Course;
import model.Student;
import repository.CourseMongoRepository;
import repository.StudentMongoRepository;
import repository.StudentRepository;
import repository.TransactionManagerMongo;
import service.AgendaService;
import view.swing.AgendaSwingView;

public class AgendaSwingViewTestIT extends AssertJSwingJUnitTestCase {
	private static String DB_NAME = "schoolagenda";
	private static String DB_COLLECTION_STUDENTS = "students";
	private static String DB_COLLECTION_COURSES = "courses";
	private static InetSocketAddress serverAddress;

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("krnbr/mongo:4.2.6").withExposedPorts(27017);

	private MongoClient client;
	private StudentMongoRepository studentRepository;
	private CourseMongoRepository courseRepository;
	private AgendaSwingView agendaSwingView;
	private AgendaController agendaController;
	private AgendaService agendaService;
	private TransactionManagerMongo transactionManager;
	private FrameFixture window;
	private MongoCollection<Document> studentCollection;
	private MongoCollection<Document> courseCollection;
	private JPanelFixture contentPanel;
	private JPanelFixture coursesPanel;

	@Override
	protected void onSetUp() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		studentRepository = new StudentMongoRepository(client, DB_NAME, DB_COLLECTION_STUDENTS);
		courseRepository = new CourseMongoRepository(client, DB_NAME, DB_COLLECTION_COURSES);
		transactionManager = new TransactionManagerMongo(client, studentRepository, courseRepository);
		agendaService = new AgendaService(transactionManager);

		GuiActionRunner.execute(() -> {
			agendaSwingView = new AgendaSwingView();
			agendaController = new AgendaController(agendaSwingView, agendaService);
			agendaSwingView.setAgendaController(agendaController);
			return agendaSwingView;
		});

		MongoDatabase database = client.getDatabase(DB_NAME);
		database.drop();
		database.createCollection(DB_COLLECTION_STUDENTS);
		database.createCollection(DB_COLLECTION_COURSES);
		studentCollection = database.getCollection(DB_COLLECTION_STUDENTS);
		courseCollection = database.getCollection(DB_COLLECTION_COURSES);

		window = new FrameFixture(robot(), agendaSwingView);
		window.show();

		contentPanel = window.panel("contentPane");
		coursesPanel = contentPanel.panel("studentTab");
	}

	private void getCoursesPanel() {
		JTabbedPaneFixture tabPanel = contentPanel.tabbedPane("tabbedPane");
		tabPanel.selectTab("Courses");
		coursesPanel = contentPanel.panel("courseTab");
	}

	private void getStudentsPanel() {
		JTabbedPaneFixture tabPanel = contentPanel.tabbedPane("tabbedPane");
		tabPanel.selectTab("Students");
		coursesPanel = contentPanel.panel("studentTab");
	}

	@Override
	protected void onTearDown() {
		client.close();
	}

	@Test
	public void testGetAllStudents() {
		// setup
		Student testStudent1 = new Student("1", "test student 1");
		Student testStudent2 = new Student("2", "test student 2");
		studentRepository.save(testStudent1);
		studentRepository.save(testStudent2);

		// exercise
		GuiActionRunner.execute(() -> {
			agendaController.getAllStudents();
		});

		// verify
		assertThat(window.list("studentsList").contents()).containsExactly(testStudent1.toString(),
				testStudent2.toString());
	}

	@Test
	public void testGetAllStudentCourses() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course", "9");
		GuiActionRunner.execute(() -> {
			agendaController.addStudent(testStudent);
			agendaController.addCourse(testCourse);
			studentRepository.updateStudentCourses(testStudent.getId(), testCourse.getId());
		});

		// execerise
		window.list("studentsList").selectItem(0);

		// verify
		assertThat(window.list("studentCoursesList").contents()).containsExactly(testCourse.toString());
	}

	@Test
	public void testAddNewStudentButtonSuccess() {
		// setup
		Student testStudent = new Student("1", "test student");
		window.textBox("studentIDTextField").enterText("1");
		window.textBox("studentNameTextField").enterText("test student");

		// exercise
		window.button("addNewStudentButton").click();

		// verify
		assertThat(window.list("studentsList").contents()).containsExactly(testStudent.toString());
		window.label("studentMessageLabel").requireText(testStudent.toString() + " successfully added!");
	}

	@Test
	public void testAddNewStudentButtonError() {
		// setup
		Student testStudent = new Student("1", "test student");
		studentRepository.save(testStudent);
		window.textBox("studentIDTextField").enterText("1");
		window.textBox("studentNameTextField").enterText("test student");

		// exercise
		window.button("addNewStudentButton").click();

		// verify
		assertThat(window.list("studentsList").contents()).isEmpty();
		window.label("studentMessageLabel").requireText("ERROR! " + testStudent.toString() + " NOT added!");
	}

	@Test
	public void testRemoveStudentButtonSuccess() {
		// setup
		Student testStudent = new Student("1", "test student");
		GuiActionRunner.execute(() -> {
			agendaController.addStudent(testStudent);
		});
		window.list("studentsList").selectItem(0);

		// exercise
		window.button("removeStudentButton").click();

		// verify
		assertThat(window.list("studentsList").contents()).isEmpty();
	}

	@Test
	public void testRemoveStudentButtonError() {
		// setup
		Student testStudent = new Student("1", "test student (not in db)");
		GuiActionRunner.execute(() -> {
			agendaSwingView.getListStudentsModel().addElement(testStudent);
		});
		window.list("studentsList").selectItem(0);

		// exercise
		window.button("removeStudentButton").click();

		// verify
		assertThat(window.list("studentsList").contents()).isEmpty();
		window.label("studentMessageLabel").requireText("ERROR! " + testStudent.toString() + " NOT removed!");
	}

	@Test
	public void testAddCourseToStudentButtonSuccess() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course", "9");

		GuiActionRunner.execute(() -> {
			agendaController.addStudent(testStudent);
			agendaController.addCourse(testCourse);
		});
		window.list("studentsList").selectItem(0);
		getCoursesPanel();
		window.list("coursesList").selectItem(0);

		// exercise
		getStudentsPanel();
		window.button("addCourseToStudentButton").click();

		// verify
		assertThat(window.list("studentCoursesList").contents()).containsExactly(testCourse.toString());
		window.label("studentMessageLabel").requireText(testCourse.toString() + " added to " + testStudent.toString());
		getCoursesPanel();
		assertThat(window.list("courseStudentsList").contents()).containsExactly(testStudent.toString());
	}

	@Test
	public void testAddCourseToStudentButtonError() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course", "9");

		GuiActionRunner.execute(() -> {
			agendaController.addStudent(testStudent);
			agendaController.addCourse(testCourse);
			agendaController.addCourseToStudent(testStudent, testCourse);
		});
		window.list("studentsList").selectItem(0);
		getCoursesPanel();
		window.list("coursesList").selectItem(0);

		// exercise
		getStudentsPanel();
		window.button("addCourseToStudentButton").click();

		// verify
		window.label("studentMessageLabel")
		.requireText("ERROR! " + testCourse.toString() + " NOT added to " + testStudent.toString());
	}

	@Test
	public void testAddStudentToCourseButtonSuccess() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course", "9");

		GuiActionRunner.execute(() -> {
			agendaController.addStudent(testStudent);
			agendaController.addCourse(testCourse);
		});
		window.list("studentsList").selectItem(0);
		getCoursesPanel();
		window.list("coursesList").selectItem(0);

		// exercise
		window.button("addStudentToCourseButton").click();

		// verify
		assertThat(window.list("courseStudentsList").contents()).containsExactly(testStudent.toString());
		window.label("courseMessageLabel").requireText(testStudent.toString() + " added to " + testCourse.toString());
		getStudentsPanel();
		assertThat(window.list("studentCoursesList").contents()).containsExactly(testCourse.toString());
	}

	@Test
	public void testAddStudentToCourseButtonError() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course", "9");

		GuiActionRunner.execute(() -> {
			agendaController.addStudent(testStudent);
			agendaController.addCourse(testCourse);
			agendaController.addStudentToCourse(testStudent, testCourse);
		});
		window.list("studentsList").selectItem(0);
		getCoursesPanel();
		window.list("coursesList").selectItem(0);

		// exercise
		window.button("addStudentToCourseButton").click();

		// verify
		window.label("courseMessageLabel")
		.requireText("ERROR! " + testStudent.toString() + " NOT added to " + testCourse.toString());
	}

	@Test
	public void testAddNewCourseButtonSuccess() {
		// setup
		getCoursesPanel();
		Course testCourse = new Course("1", "test course", "9");
		window.textBox("courseIDTextField").enterText("1");
		window.textBox("courseNameTextField").enterText("test course");
		window.textBox("courseCFUTextField").enterText("9");

		// exercise
		window.button("addNewCourseButton").click();

		// verify
		assertThat(window.list("coursesList").contents()).containsExactly(testCourse.toString());
		window.label("courseMessageLabel").requireText(testCourse.toString() + " successfully added!");
	}

	@Test
	public void testGetAllCourses() {
		// setup
		getCoursesPanel();
		Course testCourse1 = new Course("1", "test course 1", "9");
		Course testCourse2 = new Course("2", "test course 2", "9");
		courseRepository.save(testCourse1);
		courseRepository.save(testCourse2);

		// exercise
		GuiActionRunner.execute(() -> {
			agendaController.getAllCourses();
		});

		// verify
		assertThat(window.list("coursesList").contents()).containsExactly(testCourse1.toString(),
				testCourse2.toString());
	}
	
	@Test
	public void testGetAllCourseStudents() {
		// setup
		getCoursesPanel();
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent = new Student("1", "test student");
		GuiActionRunner.execute(() -> {
			agendaController.addCourse(testCourse);
			agendaController.addStudent(testStudent);
			courseRepository.updateCourseStudents(testStudent.getId(), testCourse.getId());
		});

		// execerise
		window.list("coursesList").selectItem(0);

		// verify
		assertThat(window.list("courseStudentsList").contents()).containsExactly(testStudent.toString());
	}

	@Test
	public void testAddNewCourseButtonError() {
		// setup
		getCoursesPanel();
		Course testCourse = new Course("1", "test course", "9");
		courseRepository.save(testCourse);
		window.textBox("courseIDTextField").enterText("1");
		window.textBox("courseNameTextField").enterText("test course");
		window.textBox("courseCFUTextField").enterText("9");

		// exercise
		window.button("addNewCourseButton").click();

		// verify
		assertThat(window.list("coursesList").contents()).isEmpty();
		window.label("courseMessageLabel").requireText("ERROR! " + testCourse.toString() + " NOT added!");
	}

	@Test
	public void testRemoveCourseButtonSuccess() {
		// setup
		getCoursesPanel();
		Course testCourse = new Course("1", "test course", "9");
		GuiActionRunner.execute(() -> {
			agendaController.addCourse(testCourse);
		});
		window.list("coursesList").selectItem(0);

		// exercise
		window.button("removeCourseButton").click();

		// verify
		assertThat(window.list("coursesList").contents()).isEmpty();
	}

	@Test
	public void testRemoveCourseButtonError() {
		// setup
		getCoursesPanel();
		Course testCourse = new Course("1", "test course (not in db)", "9");
		GuiActionRunner.execute(() -> {
			agendaSwingView.getListCoursesModel().addElement(testCourse);
		});
		window.list("coursesList").selectItem(0);

		// exercise
		window.button("removeCourseButton").click();

		// verify
		assertThat(window.list("coursesList").contents()).isEmpty();
		window.label("courseMessageLabel").requireText("ERROR! " + testCourse.toString() + " NOT removed!");
	}

	@Test
	public void testRemoveStudentFromCourseButtonSuccess() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent = new Student("1", "test student");
		GuiActionRunner.execute(() -> {
			agendaController.addStudent(testStudent);
			agendaController.addCourse(testCourse);
			agendaController.addStudentToCourse(testStudent, testCourse);
		});
		getCoursesPanel();
		window.list("coursesList").selectItem(0);
		window.list("courseStudentsList").selectItem(0);

		// exercise
		window.button("removeStudentFromCourseButton").click();
		
		// verify
		assertThat(window.list("courseStudentsList").contents()).doesNotContain(testStudent.toString());
		window.label("courseMessageLabel")
		.requireText(testStudent.toString() + " removed from " + testCourse.toString());
		getStudentsPanel();
		assertThat(window.list("studentCoursesList").contents()).doesNotContain(testCourse.toString());
	}

	@Test
	public void testRemoveCourseFromStudentButtonSuccess() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course", "9");
		GuiActionRunner.execute(() -> {
			agendaController.addCourse(testCourse);
			agendaController.addStudent(testStudent);
			agendaController.addCourseToStudent(testStudent, testCourse);
		});
		
		window.list("studentsList").selectItem(0);
		window.list("studentCoursesList").selectItem(0);

		// exercise
		window.button("removeCourseFromStudentButton").click();

		// verify
		assertThat(window.list("studentCoursesList").contents()).doesNotContain(testCourse.toString());
		window.label("studentMessageLabel")
		.requireText(testCourse.toString() + " removed from " + testStudent.toString());
		getCoursesPanel();
		assertThat(window.list("courseStudentsList").contents()).doesNotContain(testStudent.toString());
	}

	@Test
	public void testRemoveStudentFromCourseButtonError() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
		Student testStudent = new Student("1", "test student");
		GuiActionRunner.execute(() -> {
			agendaController.addCourse(testCourse);
			agendaController.addStudent(testStudent);
			agendaController.addStudentToCourse(testStudent, testCourse);
		});
		getCoursesPanel();
		window.list("coursesList").selectItem(0);
		window.list("courseStudentsList").selectItem(0);
		studentRepository.delete(testStudent);

		// exercise
		window.button("removeStudentFromCourseButton").click();
		
		// verify
		window.label("courseMessageLabel")
		.requireText("ERROR! " + testStudent.toString() + " NOT removed from " + testCourse.toString());
	}

	@Test
	public void testRemoveCourseFromStudentButtonError() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("2", "test course (not in db)", "9");
		GuiActionRunner.execute(() -> {
			agendaController.addStudent(testStudent);
			agendaController.addCourse(testCourse);
			agendaController.addCourseToStudent(testStudent, testCourse);
		});
		
		window.list("studentsList").selectItem(0);
		window.list("studentCoursesList").selectItem(0);
		courseRepository.delete(testCourse);

		// execerise
		window.button("removeCourseFromStudentButton").click();

		// verify
		window.label("studentMessageLabel")
		.requireText("ERROR! " + testCourse.toString() + " NOT removed from " + testStudent.toString());
	}
}
