package view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;
import static org.awaitility.Awaitility.await;
import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.AbstractContainerFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.fixture.JTabbedPaneFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;

import model.Course;
import model.Student;

@RunWith(GUITestRunner.class)
public class AgendaSwingAppE2E extends AssertJSwingJUnitTestCase {
	private static final int MONGO_PORT = 27017;
	private static final String DB_NAME = "schoolagenda";
	private static final String STUDENTS_COLLECTION_NAME = "students";
	private static final String COURSES_COLLECTION_NAME = "courses";

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

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("krnbr/mongo").withExposedPorts(MONGO_PORT);

	private MongoClient mongoClient;
	private FrameFixture window;

	private AbstractContainerFixture contentPanel;

	private JPanelFixture coursesPanel;

	@Override
	protected void onSetUp() {
		String containerIpAddress = mongo.getContainerIpAddress();
		Integer mappedPort = mongo.getMappedPort(MONGO_PORT);
		mongoClient = new MongoClient(containerIpAddress, mappedPort);
		mongoClient.startSession();
		mongoClient.getDatabase(DB_NAME).drop();

		addTestStudentToDatabase(STUDENT_1_ID, STUDENT_1_NAME, asList(COURSE_2_ID));
		addTestStudentToDatabase(STUDENT_2_ID, STUDENT_2_NAME, asList(COURSE_1_ID));
		addTestCourseToDatabase(COURSE_1_ID, COURSE_1_NAME, COURSE_1_CFU, asList(STUDENT_2_ID));
		addTestCourseToDatabase(COURSE_2_ID, COURSE_2_NAME, COURSE_2_CFU, asList(STUDENT_1_ID));

		application("app.AgendaSwingApp")
				.withArgs("--mongo-host=" + containerIpAddress, "--mongo-port=" + mappedPort.toString(),
						"--db-name=" + DB_NAME, "--db-students-collection=" + STUDENTS_COLLECTION_NAME,
						"--db-courses-collection=" + COURSES_COLLECTION_NAME, "--interface=gui")
				.start();
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "School Agenda".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());

		contentPanel = window.panel("contentPane");
		coursesPanel = contentPanel.panel("studentTab");
	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test
	@GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list("studentsList").contents())
				.anySatisfy(e -> assertThat(e).contains(STUDENT_1_ID, STUDENT_1_NAME))
				.anySatisfy(e -> assertThat(e).contains(STUDENT_2_ID, STUDENT_2_NAME));

		window.list("studentsList").selectItem(Pattern.compile(".*" + STUDENT_1_NAME + ".*"));
		assertThat(window.list("studentCoursesList").contents())
				.anySatisfy(e -> assertThat(e).contains(COURSE_2_ID, COURSE_2_NAME, COURSE_2_CFU));

		window.list("studentsList").selectItem(Pattern.compile(".*" + STUDENT_2_NAME + ".*"));
		assertThat(window.list("studentCoursesList").contents())
				.anySatisfy(e -> assertThat(e).contains(COURSE_1_ID, COURSE_1_NAME, COURSE_1_CFU));

		getCoursesPanel();
		assertThat(window.list("coursesList").contents())
				.anySatisfy(e -> assertThat(e).contains(COURSE_1_ID, COURSE_1_NAME, COURSE_1_CFU))
				.anySatisfy(e -> assertThat(e).contains(COURSE_2_ID, COURSE_2_NAME, COURSE_2_CFU));

		window.list("coursesList").selectItem(Pattern.compile(".*" + COURSE_1_NAME + ".*"));
		assertThat(window.list("courseStudentsList").contents())
				.anySatisfy(e -> assertThat(e).contains(STUDENT_2_ID, STUDENT_2_NAME));

		window.list("coursesList").selectItem(Pattern.compile(".*" + COURSE_2_NAME + ".*"));
		assertThat(window.list("courseStudentsList").contents())
				.anySatisfy(e -> assertThat(e).contains(STUDENT_1_ID, STUDENT_1_NAME));
	}

	/** Student */

	@Test
	@GUITest
	public void testAddStudentButtonSuccess() {
		Student testStudent = new Student("3", "third student");
		window.textBox("studentIDTextField").enterText("3");
		window.textBox("studentNameTextField").enterText("third student");
		window.button("addNewStudentButton").click();
		assertThat(window.list("studentsList").contents())
				.anySatisfy(e -> assertThat(e).contains("3", "third student"));
		assertThat(window.label("studentMessageLabel").text())
				.contains(testStudent.toString() + " successfully added!");
	}

	@Test
	@GUITest
	public void testAddStudentButtonError() {
		Student testStudent = new Student("1", "new first student");
		window.textBox("studentIDTextField").enterText(testStudent.getId());
		window.textBox("studentNameTextField").enterText(testStudent.getName());
		window.button("addNewStudentButton").click();
		assertThat(window.label("studentMessageLabel").text())
				.contains("ERROR! " + testStudent.toString() + " NOT added!");
	}

	@Test
	@GUITest
	public void testRemoveStudentButtonSuccess() {
		Student testStudent = new Student(STUDENT_1_ID, STUDENT_1_NAME);
		window.list("studentsList").selectItem(Pattern.compile(".*" + testStudent.getName() + ".*"));
		window.button("removeStudentButton").click();
		assertThat(window.list("studentsList").contents()).noneMatch(e -> e.contains(testStudent.toString()));
		assertThat(window.label("studentMessageLabel").text())
		.contains(testStudent.toString() + " successfully removed!");
	}

	@Test
	@GUITest
	public void testRemoveStudentButtonError() {
		Student testStudent = new Student(STUDENT_1_ID, STUDENT_1_NAME);
		window.list("studentsList").selectItems(Pattern.compile(".*" + testStudent.getName() + ".*"));
		removeTestStudentFromDatabase(testStudent.getId());
		window.button("removeStudentButton").click();
		assertThat(window.list("studentsList").contents()).doesNotContain(testStudent.toString());
		assertThat(window.label("studentMessageLabel").text())
		.contains("ERROR! " + testStudent.toString() + " NOT removed!");
	}

	@Test
	@GUITest
	public void testAddCourseToStudentButtonSuccess() {
		Student testStudent = new Student(STUDENT_1_ID, STUDENT_1_NAME);
		Course testCourse = new Course(COURSE_1_ID, COURSE_1_NAME, COURSE_1_CFU);
		window.list("studentsList").selectItem(Pattern.compile(".*" + testStudent.getName() + ".*"));
		getCoursesPanel();
		window.list("coursesList").selectItem(Pattern.compile(".*" + testCourse.getName() + ".*"));
		getStudentsPanel();
		window.button("addCourseToStudentButton").click();
		assertThat(window.list("studentCoursesList").contents()).contains(testCourse.toString());
		assertThat(window.label("studentMessageLabel").text())
		.contains(testCourse.toString() + " added to " + testStudent.toString());
	}

	@Test
	@GUITest
	public void testAddCourseToStudentButtonError() {
		Student testStudent = new Student(STUDENT_1_ID, STUDENT_1_NAME);
		Course testCourse = new Course(COURSE_2_ID, COURSE_2_NAME, COURSE_2_CFU);
		window.list("studentsList").selectItem(Pattern.compile(".*" + testStudent.getName() + ".*"));
		getCoursesPanel();
		window.list("coursesList").selectItem(Pattern.compile(".*" + testCourse.getName() + ".*"));
		getStudentsPanel();
		window.button("addCourseToStudentButton").click();
		assertThat(window.list("studentCoursesList").contents()).containsExactly(testCourse.toString());
		assertThat(window.label("studentMessageLabel").text())
		.contains(testCourse.toString() + " NOT added to " + testStudent.toString());
	}
	
	@Test
	@GUITest
	public void testRemoveCourseFromStudentButtonSuccess() {
		Student testStudent = new Student(STUDENT_1_ID, STUDENT_1_NAME);
		Course testCourse = new Course(COURSE_2_ID, COURSE_2_NAME, COURSE_2_CFU);
		window.list("studentsList").selectItem(Pattern.compile(".*" + testStudent.getName() + ".*"));
		window.list("studentCoursesList").selectItem(Pattern.compile(".*" + testCourse.getName() + ".*"));
		window.button("removeCourseFromStudentButton").click();
		assertThat(window.list("studentCoursesList").contents()).isEmpty();
		assertThat(window.label("studentMessageLabel").text())
		.contains(testCourse.toString() + " removed from " + testStudent.toString());
	}

	@Test
	@GUITest
	public void testRemoveCourseFromStudentButtonError() {
		Student testStudent = new Student(STUDENT_1_ID, STUDENT_1_NAME);
		Course testCourse = new Course(COURSE_2_ID, COURSE_2_NAME, COURSE_2_CFU);
		window.list("studentsList").selectItem(Pattern.compile(".*" + testStudent.getName() + ".*"));
		window.list("studentCoursesList").selectItem(Pattern.compile(".*" + testCourse.getName() + ".*"));
		removeTestCourseFromDatabase(testCourse.getId());
		window.button("removeCourseFromStudentButton").click();
		assertThat(window.list("studentCoursesList").contents()).isEmpty();
		assertThat(window.label("studentMessageLabel").text())
		.contains("ERROR! " + testCourse.toString() + " NOT removed from " + testStudent.toString());
	}

	/** Course */

	@Test
	@GUITest
	public void testAddCourseButtonSuccess() {
		getCoursesPanel();
		Course testCourse = new Course("3", "third course", "9");
		window.textBox("courseIDTextField").enterText("3");
		window.textBox("courseNameTextField").enterText("third course");
		window.textBox("courseCFUTextField").enterText("9");
		window.button("addNewCourseButton").click();
		assertThat(window.list("coursesList").contents())
				.anySatisfy(e -> assertThat(e).contains("3", "third course", "9"));
		assertThat(window.label("courseMessageLabel").text()).contains(testCourse.toString() + " successfully added!");
	}

	@Test
	@GUITest
	public void testAddCourseButtonError() {
		getCoursesPanel();
		Course testCourse = new Course("1", "new first course", "9");
		window.textBox("courseIDTextField").enterText("1");
		window.textBox("courseNameTextField").enterText("new first course");
		window.textBox("courseCFUTextField").enterText("9");
		window.button("addNewCourseButton").click();
		assertThat(window.label("courseMessageLabel").text())
				.contains("ERROR! " + testCourse.toString() + " NOT added!");
	}

	@Test
	@GUITest
	public void testRemoveCourseButtonSuccess() {
		getCoursesPanel();
		Course testCourse = new Course(COURSE_1_ID, COURSE_1_NAME, COURSE_1_CFU);
		window.list("coursesList").selectItem(Pattern.compile(".*" + testCourse.getName() + ".*"));
		window.button("removeCourseButton").click();
		assertThat(window.list("coursesList").contents()).noneMatch(e -> e.contains(testCourse.toString()));
		assertThat(window.label("courseMessageLabel").text())
		.contains(testCourse.toString() + " successfully removed!");
	}

	@Test
	@GUITest
	public void testRemoveCourseButtonError() {
		getCoursesPanel();
		Course testCourse = new Course(COURSE_1_ID, COURSE_1_NAME, COURSE_1_CFU);
		window.list("coursesList").selectItem(Pattern.compile(".*" + testCourse.getName() + ".*"));
		removeTestCourseFromDatabase(testCourse.getId());
		window.button("removeCourseButton").click();
		assertThat(window.list("coursesList").contents()).doesNotContain(testCourse.toString());
		assertThat(window.label("courseMessageLabel").text())
		.contains("ERROR! " + testCourse.toString() + " NOT removed!");
	}
	
	@Test
	@GUITest
	public void testAddStudentToCourseButtonSuccess() {
		getCoursesPanel();
		Course testCourse = new Course(COURSE_1_ID, COURSE_1_NAME, COURSE_1_CFU);
		Student testStudent = new Student(STUDENT_1_ID, STUDENT_1_NAME);
		window.list("coursesList").selectItem(Pattern.compile(".*" + testCourse.getName() + ".*"));
		getStudentsPanel();
		window.list("studentsList").selectItem(Pattern.compile(".*" + testStudent.getName() + ".*"));
		getCoursesPanel();
		window.button("addStudentToCourseButton").click();
		assertThat(window.list("courseStudentsList").contents()).contains(testStudent.toString());
		assertThat(window.label("courseMessageLabel").text())
		.contains(testStudent.toString() + " added to " + testCourse.toString());
	}

	@Test
	@GUITest
	public void testAddStudentToCourseButtonError() {
		getCoursesPanel();
		Course testCourse = new Course(COURSE_1_ID, COURSE_1_NAME, COURSE_1_CFU);
		Student testStudent = new Student(STUDENT_2_ID, STUDENT_2_NAME);
		window.list("coursesList").selectItem(Pattern.compile(".*" + testCourse.getName() + ".*"));
		getStudentsPanel();
		window.list("studentsList").selectItem(Pattern.compile(".*" + testStudent.getName() + ".*"));
		getCoursesPanel();
		window.button("addStudentToCourseButton").click();
		assertThat(window.list("courseStudentsList").contents()).containsExactly(testStudent.toString());
		assertThat(window.label("courseMessageLabel").text())
		.contains(testStudent.toString() + " NOT added to " + testCourse.toString());
	}
	
	@Test
	@GUITest
	public void testRemoveStudentFromCourseButtonSuccess() {
		getCoursesPanel();
		Course testCourse = new Course(COURSE_1_ID, COURSE_1_NAME, COURSE_1_CFU);
		Student testStudent = new Student(STUDENT_2_ID, STUDENT_2_NAME);
		window.list("coursesList").selectItem(Pattern.compile(".*" + testCourse.getName() + ".*"));
		window.list("courseStudentsList").selectItem(Pattern.compile(".*" + testStudent.getName() + ".*"));
		window.button("removeStudentFromCourseButton").click();
		assertThat(window.list("courseStudentsList").contents()).isEmpty();
		assertThat(window.label("courseMessageLabel").text())
		.contains(testStudent.toString() + " removed from " + testCourse.toString());
	}

	@Test
	@GUITest
	public void testRemoveStudentFromCourseButtonError() {
		getCoursesPanel();
		Course testCourse = new Course(COURSE_1_ID, COURSE_1_NAME, COURSE_1_CFU);
		Student testStudent = new Student(STUDENT_2_ID, STUDENT_2_NAME);
		window.list("coursesList").selectItem(Pattern.compile(".*" + testCourse.getName() + ".*"));
		window.list("courseStudentsList").selectItem(Pattern.compile(".*" + testStudent.getName() + ".*"));
		removeTestStudentFromDatabase(testStudent.getId());
		window.button("removeStudentFromCourseButton").click();
		assertThat(window.list("courseStudentsList").contents()).isEmpty();
		assertThat(window.label("courseMessageLabel").text())
		.contains("ERROR! " + testStudent.toString() + " NOT removed from " + testCourse.toString());
	}

	private void addTestStudentToDatabase(String id, String name, List<String> courses) {
		mongoClient.getDatabase(DB_NAME).getCollection(STUDENTS_COLLECTION_NAME)
				.insertOne(new Document().append("id", id).append("name", name).append("courses", courses));
	}

	private void addTestCourseToDatabase(String id, String name, String cfu, List<String> students) {
		mongoClient.getDatabase(DB_NAME).getCollection(COURSES_COLLECTION_NAME).insertOne(
				new Document().append("id", id).append("name", name).append("cfu", cfu).append("students", students));
	}

	private void removeTestStudentFromDatabase(String id) {
		mongoClient.getDatabase(DB_NAME).getCollection(STUDENTS_COLLECTION_NAME).deleteOne(Filters.eq("id", id));
	}

	private void removeTestCourseFromDatabase(String id) {
		mongoClient.getDatabase(DB_NAME).getCollection(COURSES_COLLECTION_NAME).deleteOne(Filters.eq("id", id));
	}

	private void getCoursesPanel() {
		await().atMost(2, TimeUnit.SECONDS).until(() -> {
			try {
				JTabbedPaneFixture tabPanel = contentPanel.tabbedPane("tabbedPane");
				tabPanel.selectTab("Courses");
				coursesPanel = contentPanel.panel("courseTab");
				return true;
			} catch (Exception e) {
				return false;
			}
		});
	}

	private void getStudentsPanel() {
		await().atMost(2, TimeUnit.SECONDS).until(() -> {
			try {
				JTabbedPaneFixture tabPanel = contentPanel.tabbedPane("tabbedPane");
				tabPanel.selectTab("Students");
				coursesPanel = contentPanel.panel("studentTab");

				return true;
			} catch (Exception e) {
				return false;
			}
		});
	}
}
