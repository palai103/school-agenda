package repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.util.Collections;

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

public class CourseMongoRepositoryTest {

	private static MongoServer mongoServer;
	private static InetSocketAddress serverAddres;

	private CourseMongoRepository courseMongoRepository;
	private MongoClient mongoClient;
	private MongoCollection<Document> courseColletion;


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
		courseColletion = mongoClient.getDatabase(DB_NAME).getCollection(DB_COLLECTION);
	}

	@After
	public void closeServer() {
		mongoClient.close();
	}
	
	@Test
	public void testFindAllCoursesWhenCollectionIsEmptyShouldReturnEmptyList() {
		assertThat(courseMongoRepository.findAll()).isEqualTo(Collections.emptyList());
	}

}
