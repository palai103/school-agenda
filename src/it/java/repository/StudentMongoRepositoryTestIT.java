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

import model.Student;

public class StudentMongoRepositoryTestIT {

	private static final String DB_NAME = "schoolagenda";
	private static final String DB_COLLECTION = "students";

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("krnbr/mongo").withExposedPorts(27017);
	private MongoClient client;
	private StudentMongoRepository studentRepository;
	private MongoCollection<Document> studentCollection;

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		studentRepository = new StudentMongoRepository(client, DB_NAME, DB_COLLECTION);
		MongoDatabase database = client.getDatabase(DB_NAME);
		database.drop();
		studentCollection = database.getCollection(DB_COLLECTION);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testFindAll() {
		addStudentToDatabase("1", "test student 1", Collections.emptyList());
		addStudentToDatabase("2", "test student 2", Collections.emptyList());
		assertThat(studentRepository.findAll()).containsExactly(new Student("1", "test student 1"),
				new Student("2", "test student 2"));
	}

	@Test
	public void testFindById() {
		addStudentToDatabase("1", "test student 1", Collections.emptyList());
		assertThat(studentRepository.findById("1")).isEqualTo(new Student("1", "test student 1"));
	}

	@Test
	public void testSave() {
		studentRepository.save(new Student("1", "test student 1"));
		assertThat(readAllStudentsFromDatabase()).containsExactly(new Student("1", "test student 1"));
	}

	@Test
	public void testDelete() {
		addStudentToDatabase("1", "test student 1", Collections.emptyList());
		addStudentToDatabase("2", "test student 2", Collections.emptyList());
		studentRepository.delete(new Student("1", "test student 1"));
		assertThat(readAllStudentsFromDatabase()).containsExactly(new Student("2", "test student 2"));
	}

	@Test
	public void testUpdateStudentCourses() {
		addStudentToDatabase("1", "test student 1", Collections.emptyList());
		studentRepository.updateStudentCourses("1", "2");
		assertThat(studentRepository.findStudentCourses("1")).containsExactly("2");
	}

	@Test
	public void testRemoveStudentCourse() {
		addStudentToDatabase("1", "test student 1", asList("2"));
		studentRepository.removeStudentCourse("1", "2");
		assertThat(studentRepository.findStudentCourses("1")).isEmpty();
	}

	@Test
	public void testFindStudentCourses() {
		addStudentToDatabase("1", "test student 1", asList("2", "3"));
		assertThat(studentRepository.findStudentCourses("1")).containsAll(asList("2", "3"));
	}

	private void addStudentToDatabase(String id, String name, List<String> courses) {
		studentCollection.insertOne(new Document().append("id", id).append("name", name).append("courses", courses));
	}

	private List<Student> readAllStudentsFromDatabase() {
		return StreamSupport.stream(studentCollection.find().spliterator(), false)
				.map(d -> new Student(d.getString("id"), d.getString("name"))).collect(Collectors.toList());
	}

}
