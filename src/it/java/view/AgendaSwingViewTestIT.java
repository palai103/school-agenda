package view;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.util.Collections;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
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
	private ClientSession clientSession;
	private AgendaSwingView agendaSwingView;
	private AgendaController agendaController;
	private AgendaService agendaService;
	private TransactionManagerMongo transactionManager;
	private FrameFixture window;
	private MongoCollection<Document> studentCollection;
	private MongoCollection<Document> courseCollection;
	private Student necessaryStudent;
	private Course necessaryCourse;

	

	@Override
	protected void onSetUp() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		clientSession = client.startSession();
		studentRepository = new StudentMongoRepository(client, DB_NAME, DB_COLLECTION_STUDENTS);
		courseRepository = new CourseMongoRepository(client, DB_NAME, DB_COLLECTION_COURSES);
		
		// explicitly empty the database through the repository
		for (Student student : studentRepository.findAll(clientSession)) {
			studentRepository.delete(clientSession, student);
		}
		
		for (Course course : courseRepository.findAll(clientSession)) {
			courseRepository.delete(clientSession, course);
		}
		
		transactionManager = new TransactionManagerMongo(client, studentRepository, courseRepository);
		agendaService = new AgendaService(transactionManager);
		
		GuiActionRunner.execute(() -> {
			agendaSwingView = new AgendaSwingView();
			agendaController = new AgendaController(agendaSwingView, agendaService);
			agendaSwingView.setAgendaController(agendaController);
			return agendaSwingView;
		});
		
		MongoDatabase database = client.getDatabase(DB_NAME);
		studentCollection = database.getCollection(DB_COLLECTION_STUDENTS);
		courseCollection = database.getCollection(DB_COLLECTION_COURSES);
		
		/**
		 * The explanation for the following lines can be found here:
		 * https://docs.mongodb.com/manual/core/transactions/
		 * 
		 * "In MongoDB 4.2 and earlier, you cannot create collections in transactions.
		 * Write operations that result in document inserts (e.g. insert or update
		 * operations with upsert: true) must be on existing collections if run inside
		 * transactions."
		 */
		necessaryStudent = new Student("0", "necessary student");
		studentCollection.insertOne(new Document().append("id", necessaryStudent.getId())
				.append("name", necessaryStudent.getName())
				.append("courses", Collections.emptyList()));
		
		necessaryCourse = new Course("0", "necessary course", "12");
		courseCollection.insertOne(new Document().append("id", necessaryCourse.getId())
				.append("name", necessaryCourse.getName())
				.append("cfu", necessaryCourse.getCFU())
				.append("students", Collections.emptyList()));
		
		window = new FrameFixture(robot(), agendaSwingView);
		window.show();
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
		studentRepository.save(clientSession, testStudent1);
		studentRepository.save(clientSession, testStudent2);

		// exercise
		GuiActionRunner.execute(() -> {
			agendaController.getAllStudents();
		});

		// verify
		assertThat(window.list("studentsList").contents()).containsExactly(necessaryStudent.toString(), 
				testStudent1.toString(), testStudent2.toString());
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
		window.label("studentAddedLabel").requireText(testStudent.toString() + " successfully added!");
	}
	
	@Test
	public void testAddNewStudentButtonError() {
		// setup
		Student testStudent = new Student("1", "test student");
		studentRepository.save(clientSession, testStudent);
		window.textBox("studentIDTextField").enterText("1");
		window.textBox("studentNameTextField").enterText("test student");

		// exercise
		window.button("addNewStudentButton").click();

		// verify
		assertThat(window.list("studentsList").contents()).isEmpty();
		window.label("studentErrorNotAddedLabel").requireText("ERROR! " + testStudent.toString() + " NOT added!");
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
		assertThat(window.list("studentsList").contents()).containsExactly(testStudent.toString());
		window.label("studentErrorNotRemovedLabel").requireText("ERROR! " + testStudent.toString() + " NOT removed!");
	}
	
// TODO: to add these kind of tests must be implemented the view for the correlation between students and courses
//	@Test
//	public void testAddCourseToStudentButtonSuccess() {
//		// setup
//		Student testStudent = new Student("1", "test student");
//		Course testCourse = new Course("1", "test course", "9");
//		studentRepository.save(clientSession, testStudent);
//		courseRepository.save(clientSession, testCourse);
//		
//		GuiActionRunner.execute(() -> {
//			agendaController.addStudent(testStudent);
//			agendaController.addCourse(testCourse);
//		});
//		window.list("studentsList").selectItem(0);
//		window.list("coursesList").selectItem(0);
//
//		// exercise
//		window.button("addCourseToStudentButton").click();
//
//		// verify
//	}
	
	@Test
	public void testAddNewCourseButtonSuccess() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
		window.textBox("courseIDTextField").enterText("1");
		window.textBox("courseNameTextField").enterText("test course");
		window.textBox("courseCFUTextField").enterText("9");

		// exercise
		window.button("addNewCourseButton").click();

		// verify
		assertThat(window.list("coursesList").contents()).containsExactly(testCourse.toString());
		window.label("courseAddedLabel").requireText(testCourse.toString() + " successfully added!");
	}
	
	@Test
	public void testGetAllCourses() {
		// setup
		Course testCourse1 = new Course("1", "test course 1", "9");
		Course testCourse2 = new Course("2", "test course 2", "9");
		courseRepository.save(clientSession, testCourse1);
		courseRepository.save(clientSession, testCourse2);

		// exercise
		GuiActionRunner.execute(() -> {
			agendaController.getAllCourses();
		});

		// verify
		assertThat(window.list("coursesList").contents()).containsExactly(necessaryCourse.toString(),
				testCourse1.toString(), testCourse2.toString());
	}
	
	@Test
	public void testAddNewCourseButtonError() {
		// setup
		Course testCourse = new Course("1", "test course", "9");
		courseRepository.save(clientSession, testCourse);
		window.textBox("courseIDTextField").enterText("1");
		window.textBox("courseNameTextField").enterText("test course");
		window.textBox("courseCFUTextField").enterText("9");

		// exercise
		window.button("addNewCourseButton").click();

		// verify
		assertThat(window.list("coursesList").contents()).isEmpty();
		window.label("courseErrorNotAddedLabel").requireText("ERROR! " + testCourse.toString() + " NOT added!");
	}
	
	@Test
	public void testRemoveCourseButtonSuccess() {
		// setup
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
		Course testCourse = new Course("1", "test course (not in db)", "9");
		GuiActionRunner.execute(() -> {
			agendaSwingView.getListCoursesModel().addElement(testCourse);
		});
		window.list("coursesList").selectItem(0);

		// exercise
		window.button("removeCourseButton").click();

		// verify
		assertThat(window.list("coursesList").contents()).containsExactly(testCourse.toString());
		window.label("courseErrorNotRemovedLabel").requireText("ERROR! " + testCourse.toString() + " NOT removed!");
	}
}
