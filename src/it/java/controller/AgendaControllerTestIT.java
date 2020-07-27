package controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import model.Course;
import model.Student;
import repository.CourseMongoRepository;
import repository.StudentMongoRepository;
import repository.TransactionManagerMongo;
import service.AgendaService;
import view.AgendaView;

public class AgendaControllerTestIT {

	private static final String DB_NAME = "schoolagenda";
	private static final String DB_COLLECTION_STUDENTS = "students";
	private static final String DB_COLLECTION_COURSES = "courses";

	@Mock
	private AgendaView agendaView;

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("krnbr/mongo").withExposedPorts(27017);
	private MongoClient client;
	private StudentMongoRepository studentMongoRepository;
	private CourseMongoRepository courseMongoRepository;
	private TransactionManagerMongo transactionManagerMongo;
	private MongoCollection<Document> studentCollection;
	private MongoCollection<Document> courseCollection;
	private AgendaService agendaService;
	private AgendaController agendaController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		studentMongoRepository = new StudentMongoRepository(client, DB_NAME, DB_COLLECTION_STUDENTS);
		courseMongoRepository = new CourseMongoRepository(client, DB_NAME, DB_COLLECTION_COURSES);
		transactionManagerMongo = new TransactionManagerMongo(client, studentMongoRepository, courseMongoRepository);
		MongoDatabase database = client.getDatabase(DB_NAME);
		database.drop();
		studentCollection = database.getCollection(DB_COLLECTION_STUDENTS);
		courseCollection = database.getCollection(DB_COLLECTION_COURSES);

		agendaService = new AgendaService(transactionManagerMongo);
		agendaController = new AgendaController(agendaView, agendaService);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testGetAllStudents() {
		// setup
		Student testStudent = new Student("1", "test student");
		agendaService.addStudent(testStudent);

		// exercise
		agendaController.getAllStudents();

		// verify
		verify(agendaView).showAllStudents(asList(testStudent));
	}
	
	@Test
	public void testAddStudent() {
		// setup
		Student testStudent = new Student("1", "test student");

		// exercise
		agendaController.addStudent(testStudent);

		// verify
		verify(agendaView).notifyStudentAdded(testStudent);
	}
	
	@Test
	public void testRemoveStudent() {
		// setup
		Student testStudent = new Student("1", "test student");
		agendaService.addStudent(testStudent);

		// exercise
		agendaController.removeStudent(testStudent);

		// verify
		verify(agendaView).notifyStudentRemoved(testStudent);
	}
	
	@Test
	public void testAddCourseToStudent() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course");
		agendaService.addStudent(testStudent);
		agendaService.addCourse(testCourse);

		// exercise
		agendaController.addCourseToStudent(testStudent, testCourse);

		// verify
		verify(agendaView).notifyCourseAddedToStudent(testStudent, testCourse);
	}
	
	@Test
	public void testRemoveCourseFromStudent() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "test course");
		agendaService.addStudent(testStudent);
		agendaService.addCourse(testCourse);
		agendaService.addCourseToStudent(testStudent, testCourse);

		// exercise
		agendaController.removeCourseFromStudent(testStudent, testCourse);

		// verify
		verify(agendaView).notifyCourseRemovedFromStudent(testStudent, testCourse);
	}
	
	@Test
	public void testAddCourse() {
		// setup
		Course testCourse = new Course("1", "test course");
		
		// exercise
		agendaController.addCourse(testCourse);

		// verify
		verify(agendaView).notifyCourseAdded(testCourse);
	}
	
	@Test
	public void testRemoveCourse() {
		// setup
		Course testCourse = new Course("1", "test course");
		agendaService.addCourse(testCourse);

		// exercise
		agendaController.removeCourse(testCourse);

		// verify
		verify(agendaView).notifyCourseRemoved(testCourse);
	}
	
	@Test
	public void testRemoveStudentFromCourse() {
		// setup
		Course testCourse = new Course("1", "test course");
		Student testStudent = new Student("1", "test student");
		agendaService.addCourse(testCourse);
		agendaService.addStudent(testStudent);
		agendaService.addCourseToStudent(testStudent, testCourse);
		
		// exercise
		agendaController.removeStudentFromCourse(testStudent, testCourse);

		// verify
		verify(agendaView).notifyStudentRemovedFromCourse(testStudent, testCourse);
	}
	
	@Test
	public void testAddStudentToCourse() {
		// setup
		Course testCourse = new Course("1", "test course");
		Student testStudent = new Student("1", "test student");
		agendaService.addCourse(testCourse);
		agendaService.addStudent(testStudent);

		// exercise
		agendaController.addStudentToCourse(testStudent, testCourse);

		// verify
		verify(agendaView).notifyStudentAddedToCourse(testStudent, testCourse);
	}
	
	@Test
	public void testGetAllCourses() {
		// setup
		Course testCourse = new Course("1", "test course");
		agendaService.addCourse(testCourse);

		// exercise
		agendaController.getAllCourses();

		// verify
		verify(agendaView).showAllCourses(asList(testCourse));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
