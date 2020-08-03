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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import model.Course;

public class CourseMongoRepositoryTestIT {

	private static final String DB_NAME = "schoolagenda";
	private static final String DB_COLLECTION = "courses";

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("krnbr/mongo").withExposedPorts(27017);

	private MongoClient client;
	private CourseMongoRepository courseRepository;
	private MongoCollection<Document> courseCollection;

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		courseRepository = new CourseMongoRepository(client, DB_NAME, DB_COLLECTION);
		MongoDatabase database = client.getDatabase(DB_NAME);
		database.drop();
		courseCollection = database.getCollection(DB_COLLECTION);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testFindAll() {
		addCourseToDatabase("1", "test course 1", Collections.emptyList());
		addCourseToDatabase("2", "test course 2", Collections.emptyList());
		assertThat(courseRepository.findAll()).containsExactly(new Course("1", "test course 1", "9"),
				new Course("2", "test course 2", "9"));
	}

	@Test
	public void testFindById() {
		addCourseToDatabase("1", "test course 1", Collections.emptyList());
		assertThat(courseRepository.findById("1")).isEqualTo(new Course("1", "test course 1", "9"));
	}

	@Test
	public void testSave() {
		courseRepository.save(new Course("1", "test course 1", "9"));
		assertThat(readAllCourseFromDatabase()).containsExactly(new Course("1", "test course 1", "9"));
	}

	@Test
	public void testDelete() {
		addCourseToDatabase("1", "test course 1", Collections.emptyList());
		addCourseToDatabase("2", "test course 2", Collections.emptyList());
		courseRepository.delete(new Course("2", "test course 2", "9"));
		assertThat(readAllCourseFromDatabase()).containsExactly(new Course("1", "test course 1", "9"));
	}

	@Test
	public void testUpdateCourseStudents() {
		addCourseToDatabase("1", "test course 1", Collections.emptyList());
		courseRepository.updateCourseStudents("2", "1");
		assertThat(courseRepository.findCourseStudents("1")).containsExactly("2");
	}
	
	@Test
	public void testRemoveCourseStudent() {
		addCourseToDatabase("1", "test course 1", asList("2"));
		courseRepository.removeCourseStudent("2", "1");
		assertThat(courseRepository.findCourseStudents("1")).isEmpty();
	}
	
	@Test
	public void testFindCourseStudents() {
		addCourseToDatabase("1", "test course 1", asList("2", "3"));
		assertThat(courseRepository.findCourseStudents("1")).containsAll(asList("2", "3"));
	}

	private void addCourseToDatabase(String id, String name, List<String> students) {
		courseCollection.insertOne(new Document().append("id", id).append("name", name).append("students", students));
	}

	private List<Course> readAllCourseFromDatabase() {
		return StreamSupport.stream(courseCollection.find().spliterator(), false)
				.map(d -> new Course(d.getString("id"), d.getString("name"), d.getString("cfu"))).collect(Collectors.toList());
	}

}
