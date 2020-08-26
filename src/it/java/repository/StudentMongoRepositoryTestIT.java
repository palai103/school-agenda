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

public class StudentMongoRepositoryTestIT {

	private static final String DB_NAME = "schoolagenda";
	private static final String DB_COLLECTION = "students";
	private static final String DB_COLLECTION_COURSES = "courses";

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("krnbr/mongo").withExposedPorts(27017);
	private MongoClient client;
	private StudentMongoRepository studentRepository;
	private MongoCollection<Document> studentCollection;
	private MongoCollection<Document> courseCollection;
	private ClientSession clientSession;

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		studentRepository = new StudentMongoRepository(client, DB_NAME, DB_COLLECTION, DB_COLLECTION_COURSES);
		MongoDatabase database = client.getDatabase(DB_NAME);
		database.drop();
		database.createCollection(DB_COLLECTION);
		database.createCollection("courses");
		studentCollection = database.getCollection(DB_COLLECTION);
		courseCollection = database.getCollection("courses");
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testFindAll() {
		addStudentToDatabase("1", "test student 1", Collections.emptyList());
		addStudentToDatabase("2", "test student 2", Collections.emptyList());
		assertThat(studentRepository.findAll(clientSession)).containsExactly(new Student("1", "test student 1"),
				new Student("2", "test student 2"));
	}

	@Test
	public void testFindById() {
		addStudentToDatabase("1", "test student 1", Collections.emptyList());
		assertThat(studentRepository.findById("1", clientSession)).isEqualTo(new Student("1", "test student 1"));
	}

	@Test
	public void testSave() {
		studentRepository.save(new Student("1", "test student 1"), clientSession);
		assertThat(readAllStudentsFromDatabase()).containsExactly(new Student("1", "test student 1"));
	}

	@Test
	public void testDelete() {
		addStudentToDatabase("1", "test student 1", Collections.emptyList());
		addStudentToDatabase("2", "test student 2", Collections.emptyList());
		studentRepository.delete(new Student("1", "test student 1"), clientSession);
		assertThat(readAllStudentsFromDatabase()).containsExactly(new Student("2", "test student 2"));
	}

	@Test
	public void testUpdateStudentCourses() {
		addStudentToDatabase("1", "test student 1", Collections.emptyList());
		addCourseToDatabase("2", "test course", "9", asList("1"));
		
		studentRepository.updateStudentCourses("1", "2", clientSession);
		assertThat(studentRepository.findStudentCourses("1", clientSession))
				.containsExactly(new Course("2", "test course", "9"));
	}

	@Test
	public void testRemoveStudentCourse() {
		addStudentToDatabase("1", "test student 1", asList("2"));
		studentRepository.removeStudentCourse("1", "2", clientSession);
		assertThat(studentRepository.findStudentCourses("1", clientSession)).isEmpty();
	}

	@Test
	public void testFindStudentCourses() {
		Student testStudent = new Student("1", "test student 1");
		Course testCourse1 = new Course("2", "test course 1", "9");
		Course testCourse2 = new Course("3", "test course 2", "9");

		addStudentToDatabase(testStudent.getId(), testStudent.getName(),
				asList(testCourse1.getId(), testCourse2.getId()));
		addCourseToDatabase(testCourse1.getId(), testCourse1.getName(), testCourse1.getCFU(),
				asList(testStudent.getId()));
		addCourseToDatabase(testCourse2.getId(), testCourse2.getName(), testCourse2.getCFU(),
				asList(testStudent.getId()));

		assertThat(studentRepository.findStudentCourses(testStudent.getId(), clientSession))
				.containsAll(asList(testCourse1, testCourse2));
	}

	private void addStudentToDatabase(String id, String name, List<String> courses) {
		studentCollection.insertOne(new Document().append("id", id).append("name", name).append("courses", courses));
	}

	private void addCourseToDatabase(String id, String name, String CFU, List<String> students) {
		courseCollection.insertOne(
				new Document().append("id", id).append("name", name).append("cfu", CFU).append("students", students));
	}

	private List<Student> readAllStudentsFromDatabase() {
		return StreamSupport.stream(studentCollection.find().spliterator(), false)
				.map(d -> new Student(d.getString("id"), d.getString("name"))).collect(Collectors.toList());
	}

}
