package repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.ServerAddress;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import model.Course;
import model.Student;

import org.bson.Document;

public class StudentMongoRepositoryTest {

	private static MongoServer mongoServer;
	private static InetSocketAddress serverAddres;

	private StudentMongoRepository studentMongoRepository;
	private MongoClient mongoClient;
	private MongoCollection<Document> studentColletion;


	private static final String DB_NAME = "schoolagenda";
	private static final String DB_COLLECTION = "students";

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
		studentMongoRepository = new StudentMongoRepository(mongoClient, DB_NAME, DB_COLLECTION);
		mongoClient.getDatabase(DB_NAME).drop();studentColletion = mongoClient.getDatabase(DB_NAME).getCollection(DB_COLLECTION);
	}

	@After
	public void closeServer() {
		mongoClient.close();
	}

	@Test
	public void testFindAllStudentsWhenCollectionIsEmptyShouldReturnEmptyList() {
		assertThat(studentMongoRepository.findAll()).isEqualTo(Collections.emptyList());
	}

	@Test
	public void testFindAllStudentsWhenCollectionIsNotEmptyShoudReturnStudentList() {
		//setup
		addTestStudentToDatabase("id", "testStudent", Collections.emptyList());

		//exercise
		List<Student> students = studentMongoRepository.findAll();

		//verify
		assertThat(students).containsExactly(new Student("id", "testStudent"));
	}

	@Test
	public void testFindStudentByIdShouldNotBeFound() {
		//verify
		assertThat(studentMongoRepository.findById("id")).isNull();
	}

	@Test
	public void testFindStudentByIdShouldBeFound() {
		//setup
		addTestStudentToDatabase("id", "testStudent", Collections.emptyList());

		//verify
		assertThat(studentMongoRepository.findById("id"))
		.isEqualTo(new Student("id", "testStudent"));
	}

	@Test
	public void testSave() {
		//setup
		Student testStudent = new Student("id", "testStudent");

		//exercise
		studentMongoRepository.save(testStudent);

		//verify
		assertThat(readAllStudentsFromDatabase()).containsExactly(testStudent);
	}

	@Test
	public void testDelete() {
		//setup
		Student testStudent = new Student("id", "testStudent");

		//exercise
		studentMongoRepository.delete(testStudent);

		//verify
		assertThat(readAllStudentsFromDatabase()).isEmpty();
	}	

	private void addTestStudentToDatabase(String id, String name, List<Course> courses) {
		studentColletion.insertOne(new Document()
				.append("id", id)
				.append("name", name)
				.append("courses", courses));
	}

	private List<Student> readAllStudentsFromDatabase() {
		return StreamSupport.
				stream(studentColletion.find().spliterator(), false)
				.map(d -> new Student(d.getString("id"), d.getString("name")))
				.collect(Collectors.toList());
	}

}
