package repository;

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

import model.Course;
import model.Student;

public class StudentMongoRepositoryTest {

	private static final String DB_NAME = "schoolagenda";
	private static final String DB_COLLECTION = "students";
	private static final String DB_COLLECTION_COURSE = "courses";

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("krnbr/mongo").withExposedPorts(27017);
	private MongoClient client;
	private StudentMongoRepository studentMongoRepository;
	private MongoCollection<Document> studentCollection;
	private MongoCollection<Document> courseCollection;

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		studentMongoRepository = new StudentMongoRepository(client, DB_NAME, DB_COLLECTION, DB_COLLECTION_COURSE);
		MongoDatabase database = client.getDatabase(DB_NAME);
		database.drop();
		database.createCollection(DB_COLLECTION);
		database.createCollection("courses");
		studentCollection = database.getCollection(DB_COLLECTION);
		courseCollection = database.getCollection("courses");
	}

	@After
	public void closeServer() {
		client.close();
	}

	@Test
	public void testFindAllStudentsWhenCollectionIsEmptyShouldReturnEmptyList() {
		assertThat(studentMongoRepository.findAll()).isEqualTo(Collections.emptyList());
	}

	@Test
	public void testFindAllStudentsWhenCollectionIsNotEmptyShoudReturnStudentList() {
		// setup
		addTestStudentToDatabase("id", "testStudent", Collections.emptyList());

		// exercise
		List<Student> students = studentMongoRepository.findAll();

		// verify
		assertThat(students).containsExactly(new Student("id", "testStudent"));
	}

	@Test
	public void testFindStudentByIdShouldNotBeFound() {
		// verify
		assertThat(studentMongoRepository.findById("id")).isNull();
	}

	@Test
	public void testFindStudentByIdShouldBeFound() {
		// setup
		addTestStudentToDatabase("id", "testStudent", Collections.emptyList());

		// verify
		assertThat(studentMongoRepository.findById("id")).isEqualTo(new Student("id", "testStudent"));
	}

	@Test
	public void testSave() {
		// setup
		Student testStudent = new Student("id", "testStudent");

		// exercise
		studentMongoRepository.save(testStudent);

		// verify
		assertThat(readAllStudentsFromDatabase()).containsExactly(testStudent);
	}

	@Test
	public void testDelete() {
		// setup
		Student testStudent1 = new Student("1", "test student 1");
		Student testStudent2 = new Student("2", "test student 2");
		addTestStudentToDatabase(testStudent1.getId(), testStudent1.getName(), Collections.emptyList());
		addTestStudentToDatabase(testStudent2.getId(), testStudent2.getName(), Collections.emptyList());

		// exercise
		studentMongoRepository.delete(testStudent1);

		// verify
		assertThat(readAllStudentsFromDatabase()).containsExactly(testStudent2);
	}

	@Test
	public void testGetCoursesFromStudentWhenCourseListIsEmpty() {
		// setup
		addTestStudentToDatabase("idStudent", "testStudent", Collections.emptyList());

		// exercise
		List<Course> studentCourses = studentMongoRepository.findStudentCourses("idStudent");

		// verify
		assertThat(studentCourses).isEqualTo(Collections.emptyList());
	}

	@Test
	public void testGetCoursesFromStudentWhenCourseListIsNotEmpty() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse1 = new Course("1", "student test course 1", "9");
		Course testCourse2 = new Course("2", "student test course 2", "9");
		addTestStudentToDatabase(testStudent.getId(), testStudent.getName(),
				asList(testCourse1.getId(), testCourse2.getId()));
		addTestCourseToDatabase(testCourse1.getId(), testCourse1.getName(), testCourse1.getCFU(),
				Collections.emptyList());
		addTestCourseToDatabase(testCourse2.getId(), testCourse2.getName(), testCourse2.getCFU(),
				Collections.emptyList());

		// exercise
		List<Course> studentCourses = studentMongoRepository.findStudentCourses(testStudent.getId());

		// verify
		assertThat(studentCourses).containsExactly(testCourse1, testCourse2);
	}

	@Test
	public void testAddCourseToStudentWhenCourseIsNotNull() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse = new Course("1", "student test course 1", "9");
		addTestStudentToDatabase(testStudent.getId(), testStudent.getName(), Collections.emptyList());
		addTestCourseToDatabase(testCourse.getId(), testCourse.getName(), testCourse.getCFU(), Collections.emptyList());

		// exercise
		studentMongoRepository.updateStudentCourses(testStudent.getId(), testCourse.getId());
		List<Course> studentCourses = studentMongoRepository.findStudentCourses(testStudent.getId());

		// verify
		assertThat(studentCourses).containsExactly(testCourse);
	}

	@Test
	public void testRemoveCourseFromStudentWhenCourseIsNotNull() {
		// setup
		Student testStudent = new Student("1", "test student");
		Course testCourse1 = new Course("1", "student test course 1", "9");
		Course testCourse2 = new Course("2", "student test course 2", "9");
		addTestStudentToDatabase(testStudent.getId(), testStudent.getName(),
				asList(testCourse1.getId(), testCourse2.getId()));
		addTestCourseToDatabase(testCourse1.getId(), testCourse1.getName(), testCourse1.getCFU(),
				Collections.emptyList());
		addTestCourseToDatabase(testCourse2.getId(), testCourse2.getName(), testCourse2.getCFU(),
				Collections.emptyList());

		// exercise
		studentMongoRepository.removeStudentCourse(testStudent.getId(), testCourse1.getId());
		List<Course> studentCourses = studentMongoRepository.findStudentCourses(testCourse1.getId());

		// verify
		assertThat(studentCourses).containsExactly(testCourse2);
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
