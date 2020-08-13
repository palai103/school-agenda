package view.cli;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Scanner;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;

public class AgendaCliAppE2E {

	private static final String STUDENT_1_ID = "1";
	private static final String STUDENT_1_NAME = "test student 1";
	private static final String STUDENT_2_ID = "2";
	private static final String STUDENT_2_NAME = "test student 2";
	private static final String COURSE_1_ID = "1";
	private static final String COURSE_1_NAME = "test course 1";
	private static final String COURSE_1_CFU = "9";
	private static final String COURSE_2_ID = "2";
	private static final String COURSE_2_NAME = "test course 2";
	private static final String COURSE_2_CFU = "9";
	public static BufferedReader testInput;
	public static BufferedWriter testOutput;
	public static Process mongo;
	public static final String mongoTestContainerName = "mongo";
	private static final String DB_NAME = "schoolagenda";
	private static final String STUDENTS_COLLECTION_NAME = "students";
	private static final String COURSES_COLLECTION_NAME = "courses";

	private MongoClient client;

	@BeforeClass
	public static void classSetup() {
		try {
			mongo = Runtime.getRuntime().exec("docker run --name mongo -p 27017:27017 --rm krnbr/mongo:4.2.6");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownContainer() {
		try {
			Runtime.getRuntime().exec("docker kill " + mongoTestContainerName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mongo.destroy();
	}

	@Before
	public void setup() {
		try {
			client = new MongoClient("localhost");
			client.getDatabase(DB_NAME).drop();
			addTestStudentToDatabase(STUDENT_1_ID, STUDENT_1_NAME, asList(COURSE_1_ID));
			addTestStudentToDatabase(STUDENT_2_ID, STUDENT_2_NAME, asList(COURSE_2_ID));
			addTestCourseToDatabase(COURSE_1_ID, COURSE_1_NAME, COURSE_1_CFU, asList(STUDENT_1_ID));
			addTestCourseToDatabase(COURSE_2_ID, COURSE_2_NAME, COURSE_2_CFU, asList(STUDENT_2_ID));

			ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar",
					"./target/school-agenda-0.0.1-SNAPSHOT-jar-with-dependencies.jar", "--interface=cli");
			processBuilder.redirectErrorStream(true);
			Process cliProcess = processBuilder.start();

			OutputStream outputStream = cliProcess.getOutputStream();
			InputStream inputStream = cliProcess.getInputStream();
			testInput = new BufferedReader(new InputStreamReader(inputStream));
			testOutput = new BufferedWriter(new OutputStreamWriter(outputStream));

			String line = null;
			boolean cliStarted = false;
			while (((line = testInput.readLine()) != null) && !cliStarted) {
				System.out.println("Process output: " + line);
				if (line.contains("---------------------------------")) {
					cliStarted = true;
					System.out.println("inizio test");
					return;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testAddNewStudentSuccess() {
		String result = getResponse("1\n3\ntest student 3\n");
		assertThat(result)
				.hasToString("Insert student id: Insert student name: Added Student [id=3, name=test student 3]");

	}

	private String getResponse(String input) {
		String result = "";
		try {
			testOutput.write(input);
			testOutput.close();
			String line = null;
			while (((line = testInput.readLine()) != null)) {
				result += line;
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private void addTestStudentToDatabase(String id, String name, List<String> courses) {
		client.getDatabase(DB_NAME).getCollection(STUDENTS_COLLECTION_NAME)
				.insertOne(new Document().append("id", id).append("name", name).append("courses", courses));
	}

	private void addTestCourseToDatabase(String id, String name, String cfu, List<String> students) {
		client.getDatabase(DB_NAME).getCollection(COURSES_COLLECTION_NAME).insertOne(
				new Document().append("id", id).append("name", name).append("cfu", cfu).append("students", students));
	}

}
