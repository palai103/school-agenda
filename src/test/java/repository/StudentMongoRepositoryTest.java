package repository;

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

public class StudentMongoRepositoryTest {

	private static final String DB_NAME = "schoolagenda";
	private static final String DB_COLLECTION = "students";

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("krnbr/mongo").withExposedPorts(27017);
	private MongoClient client;
	private ClientSession clientSession;
	private StudentMongoRepository studentMongoRepository;
	private MongoCollection<Document> studentCollection;
	private MongoCollection<Document> courseCollection;

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		clientSession = client.startSession();
		studentMongoRepository = new StudentMongoRepository(client, DB_NAME, DB_COLLECTION);
		MongoDatabase database = client.getDatabase(DB_NAME);
		database.drop();
		studentCollection = database.getCollection(DB_COLLECTION);
		courseCollection = database.getCollection("courses");
	}

	@After
	public void closeServer() {
		client.close();
	}

	@Test
	public void testFindAllStudentsWhenCollectionIsEmptyShouldReturnEmptyList() {
		assertThat(studentMongoRepository.findAll(clientSession)).isEqualTo(Collections.emptyList());
	}

	@Test
	public void testFindAllStudentsWhenCollectionIsNotEmptyShoudReturnStudentList() {
		// setup
		addTestStudentToDatabase("id", "testStudent", Collections.emptyList());

		// exercise
		List<Student> students = studentMongoRepository.findAll(clientSession);

		// verify
		assertThat(students).containsExactly(new Student("id", "testStudent"));
	}

	@Test
	public void testFindStudentByIdShouldNotBeFound() {
		// verify
		assertThat(studentMongoRepository.findById(clientSession, "id")).isNull();
	}

	@Test
	public void testFindStudentByIdShouldBeFound() {
		// setup
		addTestStudentToDatabase("id", "testStudent", Collections.emptyList());

		// verify
		assertThat(studentMongoRepository.findById(clientSession, "id")).isEqualTo(new Student("id", "testStudent"));
	}

	@Test
	public void testSave() {
		// setup
		Student testStudent = new Student("id", "testStudent");

		// exercise
		studentMongoRepository.save(clientSession, testStudent);

		// verify
		assertThat(readAllStudentsFromDatabase()).containsExactly(testStudent);
	}

	@Test
	public void testDelete() {
		// setup
		Student testStudent = new Student("id", "testStudent");

		// exercise
		studentMongoRepository.delete(clientSession, testStudent);

		// verify
		assertThat(readAllStudentsFromDatabase()).isEmpty();
	}

	@Test
	public void testGetCoursesFromStudentWhenCourseListIsEmpty() {
		// setup
		addTestStudentToDatabase("idStudent", "testStudent", Collections.emptyList());

		// exercise
		List<Course> studentCourses = studentMongoRepository.findStudentCourses(clientSession, "idStudent");

		// verify
		assertThat(studentCourses).isEqualTo(Collections.emptyList());
	}

	@Test
	public void testGetCoursesFromStudentWhenCourseListIsNotEmpty() {
		// setup
		addTestCourseToDatabase("idCourse", "test course", "9", Collections.singletonList("idStudent"));
		addTestStudentToDatabase("idStudent", "testStudent", Collections.singletonList("idCourse"));

		// exercise
		List<Course> studentCourses = studentMongoRepository.findStudentCourses(clientSession, "idStudent");

		// verify
		assertThat(studentCourses).isEqualTo(Collections.singletonList((new Course("idCourse", "test course", "9"))));
	}

	@Test
	public void testAddCourseToStudentWhenCourseIsNotNull() {
		// setup
		Student testStudent = new Student("idStudent", "testStudent");
		Course testCourse = new Course("idCourse", "testCourse", "9");

		addTestStudentToDatabase(testStudent.getId(), testStudent.getName(), Collections.emptyList());
		addTestCourseToDatabase(testCourse.getId(), testCourse.getName(), testCourse.getCFU(), Collections.emptyList());

		// exercise
		studentMongoRepository.updateStudentCourses(clientSession, "idStudent", "idCourse");
		List<Course> studentCourses = studentMongoRepository.findStudentCourses(clientSession, "idStudent");

		// verify
		assertThat(studentCourses).containsExactly(testCourse);
	}

	@Test
	public void testRemoveCourseFromStudentWhenCourseIsNotNull() {
		// setup
		addTestStudentToDatabase("idStudent", "testStudent", Collections.singletonList("idCourse"));

		// exercise
		studentMongoRepository.removeStudentCourse(clientSession, "idStudent", "idCourse");
		List<Course> studentCourses = studentMongoRepository.findStudentCourses(clientSession, "idStudent");

		// verify
		assertThat(studentCourses).isEmpty();
	}

	private void addTestStudentToDatabase(String id, String name, List<String> courses) {
		studentCollection.insertOne(new Document().append("id", id).append("name", name).append("courses", courses));
	}

	private void addTestCourseToDatabase(String id, String name, String CFU, List<String> students) {
		courseCollection.insertOne(
				new Document().append("id", id).append("name", name).append("cfu", CFU).append("students", students));
	}

	private List<Student> readAllStudentsFromDatabase() {
		return StreamSupport.stream(studentCollection.find().spliterator(), false)
				.map(d -> new Student(d.getString("id"), d.getString("name"))).collect(Collectors.toList());
	}

}
