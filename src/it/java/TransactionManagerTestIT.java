import static org.assertj.core.api.Assertions.assertThat;

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
import repository.CourseMongoRepository;
import repository.StudentMongoRepository;
import repository.TransactionManagerMongo;

public class TransactionManagerTestIT {
	private static final String DB_NAME = "schoolagenda";
	private static final String DB_COLLECTION_STUDENTS = "students";
	private static final String DB_COLLECTION_COURSES = "courses";

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("krnbr/mongo").withExposedPorts(27017);
	private TransactionManagerMongo transactionManagerMongo;
	private MongoClient client;
	private StudentMongoRepository studentMongoRepository;
	private CourseMongoRepository courseMongoRepository;
	private MongoCollection<Document> studentCollection;
	private MongoCollection<Document> courseCollection;

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		studentMongoRepository = new StudentMongoRepository(client, DB_NAME, DB_COLLECTION_STUDENTS);
		courseMongoRepository = new CourseMongoRepository(client, DB_NAME, DB_COLLECTION_COURSES);
		transactionManagerMongo = new TransactionManagerMongo(client, studentMongoRepository, courseMongoRepository);
		MongoDatabase database = client.getDatabase(DB_NAME);
		database.drop();
		studentCollection = database.getCollection(DB_COLLECTION_STUDENTS);
		courseCollection = database.getCollection(DB_COLLECTION_COURSES);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testStudentTransaction() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		studentMongoRepository.save(testStudent);

		// excercise
		Student returnedStudent = transactionManagerMongo.studentTransaction(studentRepository -> {
			return studentRepository.findById("1");
		});

		// verify
		assertThat(returnedStudent).isEqualTo(testStudent);
	}

	@Test
	public void testCourseTransaction() {
		// setup
		Course testCourse = new Course("1", "testCourse");
		courseMongoRepository.save(testCourse);

		// exercise
		Course returnedCourse = transactionManagerMongo.courseTransaction(courseRepository -> {
			return courseRepository.findById("1");
		});

		// verify
		assertThat(returnedCourse).isEqualTo(testCourse);
	}

	@Test
	public void testCompositeTransaction() {
		// setup
		Student testStudent = new Student("1", "test student 1");
		Course course = new Course("1", "test course 1");
		
		// exercise
		transactionManagerMongo.compositeTransaction((studentRepository, courseRepository) -> {
			return null;
		});
		
		// verify

	}
}
