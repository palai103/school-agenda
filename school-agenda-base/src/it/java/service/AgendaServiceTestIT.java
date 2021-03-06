package service;

import static org.assertj.core.api.Assertions.assertThat;
import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import model.Course;
import model.Student;
import repository.CourseMongoRepository;
import repository.StudentMongoRepository;
import repository.TransactionManagerMongo;

public class AgendaServiceTestIT {

	private static final String DB_NAME = "schoolagenda";
	private static final String DB_COLLECTION_STUDENTS = "students";
	private static final String DB_COLLECTION_COURSES = "courses";

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

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		studentMongoRepository = new StudentMongoRepository(client, DB_NAME, DB_COLLECTION_STUDENTS, DB_COLLECTION_COURSES);
		courseMongoRepository = new CourseMongoRepository(client, DB_NAME, DB_COLLECTION_COURSES, DB_COLLECTION_STUDENTS);
		transactionManagerMongo = new TransactionManagerMongo(client, studentMongoRepository, courseMongoRepository);
		MongoDatabase database = client.getDatabase(DB_NAME);
		database.drop();
		database.createCollection(DB_COLLECTION_STUDENTS);
		database.createCollection(DB_COLLECTION_COURSES);
		studentCollection = database.getCollection(DB_COLLECTION_STUDENTS);
		courseCollection = database.getCollection(DB_COLLECTION_COURSES);
		agendaService = new AgendaService(transactionManagerMongo);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testGetAllStudents() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		addStudentToDatabase(testStudent.getId(), testStudent.getName(), Collections.emptyList());

		// exercise
		List<Student> students = agendaService.getAllStudents();

		// verify
		assertThat(students).containsExactly(testStudent);
	}

	@Test
	public void testFindStudent() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		addStudentToDatabase(testStudent.getId(), testStudent.getName(), Collections.emptyList());

		// exercise
		Boolean foundStudent = agendaService.findStudent(testStudent);

		// verify
		assertThat(foundStudent).isTrue();
	}

	@Test
	public void testFindCourse() {
		// setup
		Course testCourse = new Course("1", "test course 1", "9");
		addCourseToDatabase(testCourse.getId(), testCourse.getName(), testCourse.getCFU(), Collections.emptyList());

		// exercise
		Boolean foundCourse = agendaService.findCourse(testCourse);

		// verify
		assertThat(foundCourse).isTrue();
	}

	@Test
	public void testAddStudent() {
		// setup
		Student testStudent = new Student("1", "test student 1");

		// exercise
		agendaService.addStudent(testStudent);

		// verify
		assertThat(readAllStudentsFromDatabase()).containsExactly(testStudent);
	}

	@Test
	public void testRemoveStudent() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		addStudentToDatabase(testStudent.getId(), testStudent.getName(), Collections.emptyList());

		// exercise
		agendaService.removeStudent(testStudent);

		// verify
		assertThat(readAllStudentsFromDatabase()).isEmpty();
	}

	@Test
	public void testAddCourseToStudent() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2", "9");
		addStudentToDatabase(testStudent.getId(), testStudent.getName(), Collections.emptyList());
		addCourseToDatabase(testCourse.getId(), testCourse.getName(), testCourse.getCFU(), Collections.emptyList());

		// exercise
		agendaService.addCourseToStudent(testStudent, testCourse);

		// verify
		assertThat(getStudentCourses(testStudent)).containsExactly(testCourse.getId());
	}

	@Test
	public void testRemoveCourseFromStudent() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2", "9");
		addStudentToDatabase(testStudent.getId(), testStudent.getName(), asList(testCourse.getId()));
		addCourseToDatabase(testCourse.getId(), testCourse.getName(), testCourse.getCFU(), asList(testStudent.getId()));

		// exercise
		agendaService.removeCourseFromStudent(testStudent, testCourse);

		// verify
		assertThat(getStudentCourses(testStudent)).isEmpty();
	}

	@Test
	public void testStudentHasCourse() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2", "9");
		addStudentToDatabase(testStudent.getId(), testStudent.getName(), asList(testCourse.getId()));
		addCourseToDatabase(testCourse.getId(), testCourse.getName(), testCourse.getCFU(), asList(testStudent.getId()));

		// exercise
		Boolean studentHasCourse = agendaService.studentHasCourse(testStudent, testCourse);

		// verify
		assertThat(studentHasCourse).isTrue();
	}

	@Test
	public void testAddCourse() {
		// setup
		Course testCourse = new Course("1", "test course 1", "9");

		// exercise
		agendaService.addCourse(testCourse);

		// verify
		assertThat(readAllCourseFromDatabase()).containsExactly(testCourse);
	}

	@Test
	public void testRemoveCourse() {
		// setup
		Course testCourse = new Course("1", "test course 1", "9");
		addCourseToDatabase(testCourse.getId(), testCourse.getName(), testCourse.getCFU(), Collections.emptyList());

		// exercise
		agendaService.removeCourse(testCourse);

		// verify
		assertThat(readAllCourseFromDatabase()).isEmpty();
	}

	@Test
	public void testCourseHasStudent() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2", "9");
		addStudentToDatabase(testStudent.getId(), testStudent.getName(), asList(testCourse.getId()));
		addCourseToDatabase(testCourse.getId(), testCourse.getName(), testCourse.getCFU(), asList(testStudent.getId()));

		// exercise
		Boolean courseHasStudent = agendaService.courseHasStudent(testStudent, testCourse);

		// verify
		assertThat(courseHasStudent).isTrue();
	}

	@Test
	public void testRemoveStudentFromCourse() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2", "9");
		addStudentToDatabase(testStudent.getId(), testStudent.getName(), asList(testCourse.getId()));
		addCourseToDatabase(testCourse.getId(), testCourse.getName(), testCourse.getCFU(), asList(testStudent.getId()));

		// exercise
		agendaService.removeStudentFromCourse(testStudent, testCourse);

		// verify
		assertThat(getCourseStudents(testCourse)).isEmpty();
	}

	@Test
	public void testAddStudentToCourse() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course testCourse = new Course("2", "test course 2", "9");
		addStudentToDatabase(testStudent.getId(), testStudent.getName(), Collections.emptyList());
		addCourseToDatabase(testCourse.getId(), testCourse.getName(), testCourse.getCFU(), Collections.emptyList());

		// exercise
		agendaService.addStudentToCourse(testStudent, testCourse);

		// verify
		assertThat(getCourseStudents(testCourse)).containsExactly(testStudent.getId());
	}

	@Test
	public void testGetAllCourses() {
		// setup
		Course testCourse = new Course("1", "test course 1", "9");
		addCourseToDatabase(testCourse.getId(), testCourse.getName(), testCourse.getCFU(), Collections.emptyList());

		// exercise
		List<Course> courses = agendaService.getAllCourses();

		// verify
		assertThat(courses).containsExactly(testCourse);
	}

	private List<String> getStudentCourses(Student student) {
		return studentCollection.find(Filters.eq("id", student.getId())).first().getList("courses", String.class);
	}

	private List<String> getCourseStudents(Course course) {
		return courseCollection.find(Filters.eq("id", course.getId())).first().getList("students", String.class);
	}

	private void addStudentToDatabase(String id, String name, List<String> courses) {
		studentCollection.insertOne(new Document().append("id", id).append("name", name).append("courses", courses));
	}

	private List<Student> readAllStudentsFromDatabase() {
		return StreamSupport.stream(studentCollection.find().spliterator(), false)
				.map(d -> new Student(d.getString("id"), d.getString("name"))).collect(Collectors.toList());
	}

	private void addCourseToDatabase(String id, String name, String CFU, List<String> students) {
		courseCollection.insertOne(
				new Document().append("id", id).append("name", name).append("cfu", CFU).append("students", students));
	}

	private List<Course> readAllCourseFromDatabase() {
		return StreamSupport.stream(courseCollection.find().spliterator(), false)
				.map(d -> new Course(d.getString("id"), d.getString("name"), d.getString("cfu")))
				.collect(Collectors.toList());
	}
}
