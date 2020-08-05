package repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import model.Course;

public class CourseMongoRepositoryTest {

	private static final String DB_NAME = "schoolagenda";
	private static final String DB_COLLECTION = "courses";
	
	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("krnbr/mongo").withExposedPorts(27017);
	private MongoClient client;
	private ClientSession clientSession;
	private CourseMongoRepository courseMongoRepository;
	private MongoCollection<Document> courseCollection;


	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		clientSession = client.startSession();
		courseMongoRepository = new CourseMongoRepository(client, DB_NAME, DB_COLLECTION);
		client.getDatabase(DB_NAME).drop();
		courseCollection = client.getDatabase(DB_NAME).getCollection(DB_COLLECTION);
	}

	@After
	public void closeServer() {
		client.close();
	}

	@Test
	public void testFindAllCoursesWhenCollectionIsEmptyShouldReturnEmptyList() {
		assertThat(courseMongoRepository.findAll(clientSession)).isEqualTo(Collections.emptyList());
	}

	@Test
	public void testFindAllCoursesWhenCollectionIsNotEmptyShoudReturnStudentList() {
		//setup
		addTestCourseToDatabase("id", "testCourse", Collections.emptyList());

		//exercise
		List<Course> courses = courseMongoRepository.findAll(clientSession);

		//verify
		assertThat(courses).containsExactly(new Course("id", "testCourse", "9"));
	}
	
	@Test
	public void testFindCourseByIdShouldNotBeFound() {
		//verify
		assertThat(courseMongoRepository.findById(clientSession, "id")).isNull();
	}
	
	@Test
	public void testFindCourseByIdShouldBeFound() {
		//setup
		addTestCourseToDatabase("id", "testCourse", Collections.emptyList());

		//verify
		assertThat(courseMongoRepository.findById(clientSession, "id"))
		.isEqualTo(new Course("id", "testCourse", "9"));
	}
	
	@Test
	public void testSave() {
		//setup
		Course testCourse = new Course("id", "testCourse", "9");

		//exercise
		courseMongoRepository.save(clientSession, testCourse);

		//verify
		assertThat(readAllCoursesFromDatabase()).containsExactly(testCourse);
	}

	@Test
	public void testDelete() {
		//setup
		Course testCourse = new Course("id", "testCourse", "9");

		//exercise
		courseMongoRepository.delete(clientSession, testCourse);

		//verify
		assertThat(readAllCoursesFromDatabase()).isEmpty();
	}
	
	@Test
	public void testGetStudentsFromCourseWhenStudentListIsEmpty() {
		//setup
		addTestCourseToDatabase("idCourse", "testCourse", Collections.emptyList());

		//exercise
		List<String> courseStudents = courseMongoRepository.findCourseStudents(clientSession, "idCourse");

		//verify
		assertThat(courseStudents).isEqualTo(Collections.emptyList());
	}
	
	@Test
	public void testGetStudentsFromCourseWhenStudentListIsNotEmpty() {
		//setup
		addTestCourseToDatabase("idCourse", "testCourse", Collections.singletonList("idStudent"));

		//exercise
		List<String> courseStudents = courseMongoRepository.findCourseStudents(clientSession, "idCourse");

		//verify
		assertThat(courseStudents).isEqualTo(Collections.singletonList("idStudent"));
	}
	
	@Test
	public void testAddStudentToCourseWhenStudentIsNotNull() {
		//setup
		addTestCourseToDatabase("idCourse", "testCourse", Collections.emptyList());

		//exercise
		courseMongoRepository.updateCourseStudents(clientSession, "idStudent", "idCourse");
		List<String> courseStudents = courseMongoRepository.findCourseStudents(clientSession, "idCourse");

		//verify
		assertThat(courseStudents).containsExactly("idStudent");
	}

	@Test
	public void testRemoveCourseToStudentWhenCourseIsNotNull() {
		//setup
		addTestCourseToDatabase("idCourse", "testCourse", Collections.singletonList("idStudent"));

		//exercise
		courseMongoRepository.removeCourseStudent(clientSession, "idStudent", "idCourse");
		List<String> courseStudents = courseMongoRepository.findCourseStudents(clientSession, "idCourse");

		//verify
		assertThat(courseStudents).isEmpty();
	}

	private void addTestCourseToDatabase(String id, String name, List<String> students) {
		courseCollection.insertOne(new Document()
				.append("id", id)
				.append("name", name)
				.append("students", students));
	}
	
	private List<Course> readAllCoursesFromDatabase() {
		return StreamSupport.
				stream(courseCollection.find().spliterator(), false)
				.map(d -> new Course(d.getString("id"), d.getString("name"), d.getString("cfu")))
				.collect(Collectors.toList());
	}

}
