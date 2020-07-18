package repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
import model.Student;

import org.bson.Document;

public class StudentMongoRepositoryTest {

	private StudentMongoRepository studentMongoRepository;
	private static MongoServer mongoServer;
	private static InetSocketAddress serverAddres;
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
		studentColletion = mongoClient.getDatabase(DB_NAME).getCollection(DB_COLLECTION);
		mongoClient.getDatabase(DB_NAME).drop();
		studentMongoRepository = new StudentMongoRepository(mongoClient, DB_NAME, DB_COLLECTION);
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
		addTestStudentToDatabase("id", "testStudent");
		
		//exercise
		List<Student> students = studentMongoRepository.findAll();
		
		//verify
		assertThat(students).containsExactly(new Student("id", "testStudent"));
	}
	
	private void addTestStudentToDatabase(String id, String name) {
		studentColletion.insertOne(new Document()
				.append("id", id)
				.append("name", name));
	}

}
