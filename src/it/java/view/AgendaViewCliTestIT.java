package view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import controller.AgendaController;
import model.Course;
import model.Student;
import repository.CourseMongoRepository;
import repository.StudentMongoRepository;
import repository.TransactionManager;
import repository.TransactionManagerMongo;
import service.AgendaService;

public class AgendaViewCliTestIT {

	private static final String DB_NAME = "schoolagenda";
	private static final String DB_COLLECTION_STUDENTS = "students";
	private static final String DB_COLLECTION_COURSES = "courses";

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("krnbr/mongo").withExposedPorts(27017);
	private AgendaViewCli agendaViewCli;
	private AgendaController agendaController;
	private AgendaService agendaService;
	private TransactionManager manager;
	private CourseMongoRepository courseRepository;
	private StudentMongoRepository studentRepository;
	private MongoClient client;
	private ByteArrayOutputStream testOutput;
	private ByteArrayInputStream testInput;
	private static final String NEWLINE = System.getProperty("line.separator");

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		studentRepository = new StudentMongoRepository(client, DB_NAME, DB_COLLECTION_STUDENTS);
		courseRepository = new CourseMongoRepository(client, DB_NAME, DB_COLLECTION_COURSES);
		manager = new TransactionManagerMongo(client, studentRepository, courseRepository);
		MongoDatabase database = client.getDatabase(DB_NAME);
		database.drop();
		testOutput = new ByteArrayOutputStream();
		agendaViewCli = new AgendaViewCli(System.in, new PrintStream(testOutput));
		agendaService = new AgendaService(manager);
		agendaController = new AgendaController(agendaViewCli, agendaService);
		agendaViewCli.inject(agendaController);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testShowAllStudents() {
		// setup
		Student testStudent1 = new Student("1", "test student 1");
		Student testStudent2 = new Student("2", "test student 2");
		agendaService.addStudent(testStudent1);
		agendaService.addStudent(testStudent2);

		// exercise
		agendaController.getAllStudents();

		// verify
		assertThat(testOutput.toString()).hasToString(
				"Student [id=1, name=test student 1]" + NEWLINE + "Student [id=2, name=test student 2]" + NEWLINE);
	}

	@Test
	public void testAddStudent() {
		// Setup
		String userInput = "3\n1\ntest student";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		agendaViewCli.setInput(testInput);

		// Exercise
		agendaViewCli.menuChoice();

		// verify
		assertThat(testOutput.toString())
				.contains("Insert student id: Insert student name: Added Student [id=1, name=test student]");
	}
	
	@Test
	public void testAddStudentNotAdded() {
		// Setup
		Student testStudent1 = new Student("1", "test student 1");
		agendaService.addStudent(testStudent1);
		String userInput = "3\n1\ntest student";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		agendaViewCli.setInput(testInput);

		// Exercise
		agendaViewCli.menuChoice();

		// verify
		assertThat(testOutput.toString())
				.contains("Insert student id: Insert student name: Student [id=1, name=test student] not added");
	}
	
	@Test
	public void testRemoveStudent() {
		// Setup
		Student testStudent1 = new Student("1", "test student 1");
		agendaService.addStudent(testStudent1);
		String userInput = "9\n1\n";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		agendaViewCli.setInput(testInput);

		// Exercise
		agendaViewCli.menuChoice();

		// verify
		assertThat(testOutput.toString())
				.contains("Insert student id: Student with id 1 removed");
	}
	
	@Test
	public void testRemoveStudentNotRemoved() {
		// Setup
		String userInput = "9\n1\n";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		agendaViewCli.setInput(testInput);

		// Exercise
		agendaViewCli.menuChoice();

		// verify
		assertThat(testOutput.toString())
				.contains("Insert student id: Student with id 1 not removed");
	}
	
	@Test
	public void testAddCourseToStudent() {
		// setup
		Student testStudent1 = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2");
		agendaService.addStudent(testStudent1);
		agendaService.addCourse(testCourse);
		String userInput = "5\n1\n2\n";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		agendaViewCli.setInput(testInput);
		
		// exercise
		agendaViewCli.menuChoice();
		
		// verify
		assertThat(testOutput.toString())
		.contains("Insert student id: Insert course id: Course with id 2 added to student with id 1");
	}
	
	@Test
	public void testAddCourseToStudentNotAdded() {
		// setup
		String userInput = "5\n1\n2\n";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		agendaViewCli.setInput(testInput);
		
		// exercise
		agendaViewCli.menuChoice();
		
		// verify
		assertThat(testOutput.toString())
		.contains("Insert student id: Insert course id: Course with id 2 not added to student with id 1");
	}
	
	@Test
	public void testRemoveCourseFromStudent() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2");
		agendaService.addStudent(testStudent);
		agendaService.addCourse(testCourse);
		agendaService.addCourseToStudent(testStudent, testCourse);
		String userInput = "7\n1\n2\n";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		agendaViewCli.setInput(testInput);
		
		// exercise
		agendaViewCli.menuChoice();
		
		// verify
		assertThat(testOutput.toString())
		.contains("Insert student id: Insert course id: Course with id 2 removed from student with id 1");
	}
	
	@Test
	public void testRemoveCourseFromStudentShouldNotRemove() {
		// setup
		String userInput = "7\n1\n2\n";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		agendaViewCli.setInput(testInput);
		
		// exercise
		agendaViewCli.menuChoice();
		
		// verify
		assertThat(testOutput.toString())
		.contains("Insert student id: Insert course id: Course with id 2 not removed from student with id 1");
	}
	
	@Test
	public void testAddCourse() {
		// Setup
		String userInput = "4\n1\ntest course";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		agendaViewCli.setInput(testInput);

		// Exercise
		agendaViewCli.menuChoice();

		// verify
		assertThat(testOutput.toString())
				.contains("Insert course id: Insert course name: Added Course [id=1, name=test course]");
	}
	
	@Test
	public void testAddCourseNotAdded() {
		// Setup
		Course testCourse = new Course("1", "test course 1");
		agendaService.addCourse(testCourse);
		String userInput = "4\n1\ntest course";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		agendaViewCli.setInput(testInput);

		// Exercise
		agendaViewCli.menuChoice();

		// verify
		assertThat(testOutput.toString())
				.contains("Insert course id: Insert course name: Course [id=1, name=test course] not added");
	}
	
	@Test
	public void testAddStudentToCourse() {
		// setup
		Student testStudent1 = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2");
		agendaService.addStudent(testStudent1);
		agendaService.addCourse(testCourse);
		String userInput = "6\n1\n2\n";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		agendaViewCli.setInput(testInput);
		
		// exercise
		agendaViewCli.menuChoice();
		
		// verify
		assertThat(testOutput.toString())
		.contains("Insert student id: Insert course id: Student with id 1 added to course with id 2");
	}
	
	@Test
	public void testAddStudentToCourseNotAdded() {
		// setup
		String userInput = "6\n1\n2\n";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		agendaViewCli.setInput(testInput);
		
		// exercise
		agendaViewCli.menuChoice();
		
		// verify
		assertThat(testOutput.toString())
		.contains("Insert student id: Insert course id: Student with id 1 not added to course with id 2");
	}
	
	@Test
	public void testRemoveStudentFromCourse() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2");
		agendaService.addStudent(testStudent);
		agendaService.addCourse(testCourse);
		agendaService.addStudentToCourse(testStudent, testCourse);
		String userInput = "8\n1\n2\n";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		agendaViewCli.setInput(testInput);
		
		// exercise
		agendaViewCli.menuChoice();
		
		// verify
		assertThat(testOutput.toString())
		.contains("Insert student id: Insert course id: Student with id 1 removed from course with id 2");
	}
	
	@Test
	public void testRemoveStudentFromCourseShouldNotRemove() {
		// setup
		String userInput = "8\n1\n2\n";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		agendaViewCli.setInput(testInput);
		
		// exercise
		agendaViewCli.menuChoice();
		
		// verify
		assertThat(testOutput.toString())
		.contains("Insert student id: Insert course id: Student with id 1 not removed from course with id 2");
	}
	
	@Test
	public void testShowAllCourses() {
		// setup
		Course testCourse1 = new Course("1", "test course 1");
		Course testCourse2 = new Course("2", "test course 2");
		agendaService.addCourse(testCourse1);
		agendaService.addCourse(testCourse2);

		// exercise
		agendaController.getAllCourses();

		// verify
		assertThat(testOutput.toString()).hasToString(
				"Course [id=1, name=test course 1]" + NEWLINE + "Course [id=2, name=test course 2]" + NEWLINE);
	}
	
	@Test
	public void testRemoveCourse() {
		// Setup
		Course testCourse = new Course("1", "test course 1");
		agendaService.addCourse(testCourse);
		String userInput = "10\n1\n";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		agendaViewCli.setInput(testInput);

		// Exercise
		agendaViewCli.menuChoice();

		// verify
		assertThat(testOutput.toString())
				.contains("Insert course id: Course with id 1 removed");
	}
	
	@Test
	public void testRemoveCourseNotRemoved() {
		// Setup
		String userInput = "10\n1\n";
		testInput = new ByteArrayInputStream(userInput.getBytes());
		agendaViewCli.setInput(testInput);

		// Exercise
		agendaViewCli.menuChoice();

		// verify
		assertThat(testOutput.toString())
				.contains("Insert course id: Course with id 1 not removed");
	}

}
