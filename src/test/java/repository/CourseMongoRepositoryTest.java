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
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import model.Course;

public class CourseMongoRepositoryTest {

	private static MongoServer mongoServer;
	private static InetSocketAddress serverAddres;

	private CourseMongoRepository courseMongoRepository;
	private MongoClient mongoClient;
	private MongoCollection<Document> courseCollection;


	private static final String DB_NAME = "schoolagenda";
	private static final String DB_COLLECTION = "courses";

	@BeforeClass
	public static void initServer() {
		mongoServer = new MongoServer(new MemoryBackend());
		serverAddres = mongoServer.bind();
	}

	@AfterClass
	public static void shutdownServer() {
		mongoServer.shutdown();
	}

	@Before
	public void setup() {
		mongoClient = new MongoClient(new ServerAddress(serverAddres));
		courseMongoRepository = new CourseMongoRepository(mongoClient, DB_NAME, DB_COLLECTION);
		mongoClient.getDatabase(DB_NAME).drop();
		courseCollection = mongoClient.getDatabase(DB_NAME).getCollection(DB_COLLECTION);
	}

	@After
	public void closeServer() {
		mongoClient.close();
	}

	@Test
	public void testFindAllCoursesWhenCollectionIsEmptyShouldReturnEmptyList() {
		assertThat(courseMongoRepository.findAll()).isEqualTo(Collections.emptyList());
	}

	@Test
	public void testFindAllCoursesWhenCollectionIsNotEmptyShoudReturnStudentList() {
		//setup
		addTestCourseToDatabase("id", "testCourse", Collections.emptyList());

		//exercise
		List<Course> courses = courseMongoRepository.findAll();

		//verify
		assertThat(courses).containsExactly(new Course("id", "testCourse"));
	}
	
	@Test
	public void testFindCourseByIdShouldNotBeFound() {
		//verify
		assertThat(courseMongoRepository.findById("id")).isNull();
	}
	
	@Test
	public void testFindCourseByIdShouldBeFound() {
		//setup
		addTestCourseToDatabase("id", "testCourse", Collections.emptyList());

		//verify
		assertThat(courseMongoRepository.findById("id"))
		.isEqualTo(new Course("id", "testCourse"));
	}
	
	@Test
	public void testSave() {
		//setup
		Course testCourse = new Course("id", "testCourse");

		//exercise
		courseMongoRepository.save(testCourse);

		//verify
		assertThat(readAllCoursesFromDatabase()).containsExactly(testCourse);
	}

	@Test
	public void testDelete() {
		//setup
		Course testCourse = new Course("id", "testCourse");

		//exercise
		courseMongoRepository.delete(testCourse);

		//verify
		assertThat(readAllCoursesFromDatabase()).isEmpty();
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
				.map(d -> new Course(d.getString("id"), d.getString("name")))
				.collect(Collectors.toList());
	}

}
