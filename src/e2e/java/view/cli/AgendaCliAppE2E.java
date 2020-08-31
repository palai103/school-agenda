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
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

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
	private static final String NEWLINE = ""; /* necessary because the BufferedReader does not interpret \n chars */
	private static final String LOOP_MESSAGE = "--------- Pick a choice: ---------" + NEWLINE + "1) Show all students"
			+ NEWLINE + "2) Show all courses" + NEWLINE + "3) Add a student" + NEWLINE + "4) Add a course" + NEWLINE
			+ "5) Enroll a student to a course (by student)" + NEWLINE + "6) Enroll a student to a course (by course)"
			+ NEWLINE + "7) Delete a student enrollment (by student id)" + NEWLINE
			+ "8) Delete a student enrollment (by course id)" + NEWLINE + "9) Delete a student" + NEWLINE
			+ "10) Delete a course" + NEWLINE + "11) Show all student courses" + NEWLINE
			+ "12) Show all course students" + NEWLINE + "13) Exit" + NEWLINE + "---------------------------------";

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
	public void testAddNewStudentSuccess() throws IOException {
		String result = getResponse("3\n3\ntest student\n");
		assertThat(result).contains("Insert student id: Insert student name: Added Student [id=3, name=test student]");
	}

	@Test
	public void testAddNewStudentError() throws IOException {
		String result = getResponse("3\n" + STUDENT_1_ID + "\ntest student\n");
		assertThat(result).contains("Insert student id: Insert student name: Student [id=" + STUDENT_1_ID
				+ ", name=test student] not added");
	}

	@Test
	public void testGetAllSudentsWhenDbIsPopulated() throws IOException {
		String result = getResponse("1\n");
		assertThat(result).contains("Student [id=" + STUDENT_1_ID + ", name=" + STUDENT_1_NAME + "]Student [id="
				+ STUDENT_2_ID + ", name=" + STUDENT_2_NAME + "]");
	}

	@Test
	public void testGetAllSudentsWhenDbIsEmpty() throws IOException {
		removeTestStudentFromDatabase(STUDENT_1_ID);
		removeTestStudentFromDatabase(STUDENT_2_ID);
		String result = getResponse("1\n");
		assertThat(result).contains("" + LOOP_MESSAGE);
	}

	@Test
	public void testRemoveStudentSuccess() throws IOException {
		String result = getResponse("9\n2\n");
		assertThat(result).contains("Insert student id: Student with id 2 removed");
	}

	@Test
	public void testRemoveStudentError() throws IOException {
		String result = getResponse("9\n5\n");
		assertThat(result).contains("Insert student id: Student with id 5 not removed");
	}

	@Test
	public void testAddNewCourseSuccess() throws IOException {
		String result = getResponse("4\n3\ntest course\n9\n");
		assertThat(result).contains(
				"Insert course id: Insert course name: Insert course CFU: Added Course [id=3, name=test course, CFU=9]");
	}

	@Test
	public void testAddNewCourseError() throws IOException {
		String result = getResponse("4\n" + COURSE_1_ID + "\ntest course\n9");
		assertThat(result).contains("Insert course id: Insert course name: Insert course CFU: Course [id=" + COURSE_1_ID
				+ ", name=test course, CFU=9] not added");
	}

	@Test
	public void testGetAllCoursesWhenDbIsPopulated() throws IOException {
		String result = getResponse("2\n");
		assertThat(result).contains("Course [id=" + COURSE_1_ID + ", name=" + COURSE_1_NAME + ", CFU=" + COURSE_1_CFU
				+ "]Course [id=" + COURSE_2_ID + ", name=" + COURSE_2_NAME + ", CFU=" + COURSE_1_CFU + "]");
	}

	@Test
	public void testGetAllCoursesWhenDbIsEmpty() throws IOException {
		removeTestCourseFromDatabase(COURSE_1_ID);
		removeTestCourseFromDatabase(COURSE_2_ID);
		String result = getResponse("2\n");
		assertThat(result).contains("" + LOOP_MESSAGE);
	}

	@Test
	public void testRemoveCourseSuccess() throws IOException {
		String result = getResponse("10\n2\n");
		assertThat(result).contains("Insert course id: Course with id 2 removed");
	}

	@Test
	public void testRemoveCourseError() throws IOException {
		String result = getResponse("10\n5\n");
		assertThat(result).contains("Insert course id: Course with id 5 not removed");
	}

	@Test
	public void testAddCourseToStudentSuccess() throws IOException {
		String result = getResponse("5\n" + STUDENT_1_ID + "\n" + COURSE_2_ID + "\n");
		assertThat(result).contains("Insert student id: Insert course id: Course with id " + COURSE_2_ID
				+ " added to student with id " + STUDENT_1_ID);
	}

	@Test
	public void testAddCourseToStudentError() throws IOException {
		String result = getResponse("5\n" + STUDENT_1_ID + "\n" + 5 + "\n");
		assertThat(result).contains(
				"Insert student id: Insert course id: Course with id 5 not added to student with id " + STUDENT_1_ID);
	}

	@Test
	public void testAddStudentToCourseSuccess() throws IOException {
		String result = getResponse("6\n" + STUDENT_2_ID + "\n" + COURSE_1_ID + "\n");
		assertThat(result).contains("Insert student id: Insert course id: Student with id " + STUDENT_2_ID
				+ " added to course with id " + COURSE_1_ID);
	}

	@Test
	public void testAddStudentToCourseError() throws IOException {
		String result = getResponse("6\n" + STUDENT_1_ID + "\n" + 5 + "\n");
		assertThat(result)
				.contains("Insert student id: Insert course id: Student with id 1 not added to course with id " + 5);
	}

	@Test
	public void testRemoveCourseToStudentSuccess() throws IOException, InterruptedException {
		String result = getResponse("7\n1\n1\n");
		assertThat(result)
				.contains("Insert student id: Insert course id: Course with id 1 removed from student with id 1");
	}

	@Test
	public void testRemoveCourseToStudentError() {
		String result = getResponse("7\n" + STUDENT_1_ID + "\n" + 5 + "\n");
		assertThat(result).contains("Insert student id: Insert course id: Course with id " + 5
				+ " not removed from student with id " + STUDENT_1_ID);
	}

	@Test
	public void testRemoveStudentToCourseSuccess() {
		String result = getResponse("8\n" + STUDENT_1_ID + "\n" + COURSE_1_ID + "\n");
		assertThat(result).contains("Insert student id: Insert course id: Student with id " + STUDENT_1_ID
				+ " removed from course with id " + COURSE_1_ID);
	}

	@Test
	public void testRemoveStudentToCourseError() {
		String result = getResponse("8\n" + STUDENT_1_ID + "\n" + 5 + "\n");
		assertThat(result).contains("Insert student id: Insert course id: Student with id " + STUDENT_1_ID
				+ " not removed from course with id " + 5);
	}

	@Test
	public void testShowAllStudentsCoursesWhenCoursesAreNotEmpty() {
		String result = getResponse("11\n1\n");
		assertThat(result).contains("Insert student id: Course [id=" + COURSE_1_ID + ", name=" + COURSE_1_NAME
				+ ", CFU=" + COURSE_1_CFU + "]");
	}

	@Test
	public void testShowAllStudentsCoursesWhenCoursesAreEmpty() {
		addTestStudentToDatabase("3", "test student 3", Collections.emptyList());
		String result = getResponse("11\n3\n");
		assertThat(result).contains("Insert student id: " + LOOP_MESSAGE);
	}

	@Test
	public void testShowAllCoursesStudentsWhenStudentsAreNotEmpty() {
		String result = getResponse("12\n1\n");
		assertThat(result).contains("Insert course id: Student [id=" + STUDENT_1_ID + ", name=" + STUDENT_1_NAME + "]");
	}

	@Test
	public void testShowAllCoursesStudentsWhenStudentsAreEmpty() {
		addTestCourseToDatabase("3", "test course 3", "9", Collections.emptyList());
		String result = getResponse("12\n3\n");
		assertThat(result).contains("Insert course id: " + LOOP_MESSAGE);
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

	private void removeTestStudentFromDatabase(String id) {
		client.getDatabase(DB_NAME).getCollection(STUDENTS_COLLECTION_NAME).deleteOne(Filters.eq("id", id));
	}

	private void removeTestCourseFromDatabase(String id) {
		client.getDatabase(DB_NAME).getCollection(COURSES_COLLECTION_NAME).deleteOne(Filters.eq("id", id));
	}

}
