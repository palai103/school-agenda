import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
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
import repository.CourseMongoRepository;

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
		addCourseToDatabse("1", "test course 1", Collections.emptyList());
		addCourseToDatabse("2", "test course 2", Collections.emptyList());
		assertThat(courseRepository.findAll()).containsExactly(new Course("1", "test course 1"),
				new Course("2", "test course 2"));
	}

	@Test
	public void testFindById() {
		addCourseToDatabse("1", "test course 1", Collections.emptyList());
		assertThat(courseRepository.findById("1")).isEqualTo(new Course("1", "test course 1"));
	}

	@Test
	public void testSave() {
		courseRepository.save(new Course("1", "test course 1"));
		assertThat(readAllCourseFromDatabase()).containsExactly(new Course("1", "test course 1"));
	}
	
	@Test
	public void testDelete() {
		addCourseToDatabse("1", "test course 1", Collections.emptyList());
		addCourseToDatabse("2", "test course 2", Collections.emptyList());
		courseRepository.delete(new Course("2", "test course 2"));
		assertThat(readAllCourseFromDatabase()).containsExactly(new Course("1", "test course 1"));
	}

	private void addCourseToDatabse(String id, String name, List<String> students) {
		courseCollection.insertOne(new Document().append("id", id).append("name", name).append("students", students));
	}

	private List<Course> readAllCourseFromDatabase() {
		return StreamSupport.stream(courseCollection.find().spliterator(), false)
				.map(d -> new Course(d.getString("id"), d.getString("name"))).collect(Collectors.toList());
	}

}
