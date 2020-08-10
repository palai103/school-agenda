package view.swing;
import static org.assertj.swing.launcher.ApplicationLauncher.application;
import static org.awaitility.Awaitility.await;

import java.awt.Frame;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.driver.FrameDriver;
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

import model.Course;
import model.Student;

@RunWith(GUITestRunner.class)
public class AgendaSwingAppE2E extends AssertJSwingJUnitTestCase {

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("krnbr/mongo").withExposedPorts(27017);

	private static final String DB_NAME = "schoolagenda";
	private static final String STUDENTS_COLLECTION_NAME = "students";
	private static final String COURSES_COLLECTION_NAME = "courses";

	private MongoClient mongoClient;
	private FrameFixture window;

	private AbstractContainerFixture contentPanel;

	private JPanelFixture coursesPanel;

	@Override
	protected void onSetUp() {
		String containerIpAddress = mongo.getContainerIpAddress();
		Integer mappedPort = mongo.getMappedPort(27017);
		mongoClient = new MongoClient(containerIpAddress, mappedPort);
		mongoClient.startSession();
		mongoClient.getDatabase(DB_NAME).drop();
		addTestStudentToDatabase("1", "first student");
		addTestStudentToDatabase("2", "second student");
		addTestCourseToDatabase("1", "first course", "9");
		addTestCourseToDatabase("2", "second course", "9");

		application("view.swing.AgendaSwingApp")
				.withArgs("--mongo-host=" + containerIpAddress, "--mongo-port=" + mappedPort.toString(),
						"--db-name=" + DB_NAME, "--db-students-collection=" + STUDENTS_COLLECTION_NAME,
						"--db-courses-collection=" + COURSES_COLLECTION_NAME)
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

	@Test
	@GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list("studentsList").contents()).anySatisfy(e -> assertThat(e).contains("1", "first student"))
				.anySatisfy(e -> assertThat(e).contains("2", "second student"));
	}

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
	public void testRemoveStudentButtonSuccess() {
		Student testStudent = new Student("1", "first student");
		window.list("studentsList").selectItem(Pattern.compile(".*" + testStudent.getName() + ".*"));
		window.button("removeStudentButton").click();
		assertThat(window.list("studentsList").contents()).noneMatch(e -> e.contains(testStudent.toString()));
	}

	private void addTestStudentToDatabase(String id, String name) {
		mongoClient.getDatabase(DB_NAME).getCollection(STUDENTS_COLLECTION_NAME)
				.insertOne(new Document().append("id", id).append("name", name));

	}

	private void addTestCourseToDatabase(String id, String name, String cfu) {
		mongoClient.getDatabase(DB_NAME).getCollection(COURSES_COLLECTION_NAME)
				.insertOne(new Document().append("id", id).append("name", name).append("cfu", cfu));

	}

}
