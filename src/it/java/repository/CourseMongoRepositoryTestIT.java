package repository;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

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
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import model.Course;
import model.Student;

public class CourseMongoRepositoryTestIT {

	private static final String DB_NAME = "schoolagenda";
	private static final String DB_COLLECTION = "courses";
	private static final String DB_COLLECTION_STUDENTS = "students";

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("krnbr/mongo").withExposedPorts(27017);

	private MongoClient client;
	private CourseMongoRepository courseRepository;
	private MongoCollection<Document> courseCollection;
	private MongoCollection<Document> studentCollection;
	private ClientSession clientSession;

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		clientSession = client.startSession();
		courseRepository = new CourseMongoRepository(client, DB_NAME, DB_COLLECTION, DB_COLLECTION_STUDENTS);
		MongoDatabase database = client.getDatabase(DB_NAME);
		database.drop();
		database.createCollection(DB_COLLECTION);
		database.createCollection("students");
		courseCollection = database.getCollection(DB_COLLECTION);
		studentCollection = database.getCollection("students");
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testFindAll() {
		addCourseToDatabase("1", "test course 1", "9", Collections.emptyList());
		addCourseToDatabase("2", "test course 2", "9", Collections.emptyList());
		assertThat(courseRepository.findAll(clientSession)).containsExactly(new Course("1", "test course 1", "9"),
				new Course("2", "test course 2", "9"));
	}

	@Test
	public void testFindById() {
		addCourseToDatabase("1", "test course 1", "9", Collections.emptyList());
		assertThat(courseRepository.findById("1", clientSession)).isEqualTo(new Course("1", "test course 1", "9"));
	}

	@Test
	public void testSave() {
		courseRepository.save(new Course("1", "test course 1", "9"), clientSession);
		assertThat(readAllCourseFromDatabase()).containsExactly(new Course("1", "test course 1", "9"));
	}

	@Test
	public void testDelete() {
		addCourseToDatabase("1", "test course 1", "9", Collections.emptyList());
		addCourseToDatabase("2", "test course 2", "9", Collections.emptyList());
		courseRepository.delete(new Course("2", "test course 2", "9"), clientSession);
		assertThat(readAllCourseFromDatabase()).containsExactly(new Course("1", "test course 1", "9"));
	}

	@Test
	public void testUpdateCourseStudents() {
		addCourseToDatabase("1", "test course 1", "9", Collections.emptyList());
		addStudentToDatabase("2", "test student", asList("1"));

		courseRepository.updateCourseStudents("2", "1", clientSession);
		assertThat(courseRepository.findCourseStudents("1", clientSession))
				.containsExactly(new Student("2", "test student"));
	}

	@Test
	public void testRemoveCourseStudent() {
		addCourseToDatabase("1", "test course 1", "9", asList("2"));
		courseRepository.removeCourseStudent("2", "1", clientSession);
		assertThat(courseRepository.findCourseStudents("1", clientSession)).isEmpty();
	}

	@Test
	public void testFindCourseStudents() {
		Course testCourse = new Course("1", "test course 1", "9");
		Student testStudent1 = new Student("2", "test student 1");
		Student testStudent2 = new Student("3", "test student 2");

		addCourseToDatabase(testCourse.getId(), testCourse.getName(), testCourse.getCFU(),
				asList(testStudent1.getId(), testStudent2.getId()));
		addStudentToDatabase(testStudent1.getId(), testStudent1.getName(), asList(testCourse.getId()));
		addStudentToDatabase(testStudent2.getId(), testStudent2.getName(), asList(testCourse.getId()));

		assertThat(courseRepository.findCourseStudents(testCourse.getId(), clientSession))
				.containsAll(asList(testStudent1, testStudent2));
	}

	private void addCourseToDatabase(String id, String name, String CFU, List<String> students) {
		courseCollection.insertOne(
				new Document().append("id", id).append("name", name).append("cfu", CFU).append("students", students));
	}

	private void addStudentToDatabase(String id, String name, List<String> courses) {
		studentCollection.insertOne(new Document().append("id", id).append("name", name).append("courses", courses));
	}

	private List<Course> readAllCourseFromDatabase() {
		return StreamSupport.stream(courseCollection.find().spliterator(), false)
				.map(d -> new Course(d.getString("id"), d.getString("name"), d.getString("cfu")))
				.collect(Collectors.toList());
	}

}
