package view.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import controller.AgendaController;
import model.Course;
import model.Student;
import view.AgendaView;

public class AgendaSwingView extends JFrame implements AgendaView {

	private static final String ERROR = "ERROR! ";

	/**
	 * 
	 */
	private static final long serialVersionUID = -5916915210587910682L;

	private transient AgendaController agendaController;

	private JPanel contentPane;
	private JTextField fieldStudentId;
	private JTextField fieldCourseId;
	private JTextField fieldStudentName;
	private JTextField fieldCourseName;
	private JTextField fieldCourseCFU;

	private DefaultListModel<Student> studentsListModel;
	private DefaultListModel<Course> coursesListModel;
	private DefaultListModel<Course> studentCoursesListModel;
	private DefaultListModel<Student> courseStudentsListModel;

	private JLabel lblCourseMessage;
	private JLabel lblStudentMessage;

	private JList<Student> studentsList;
	private JList<Course> coursesList;
	private JList<Course> studentCoursesList;
	private JList<Student> courseStudentsList;

	private JButton btnAddNewCourse;
	private JButton btnAddStudentToCourse;
	private JButton btnRemoveCourse;
	private JButton btnRemoveStudentFromCourse;
	private JButton btnAddNewStudent;
	private JButton btnAddCourseToStudent;
	private JButton btnRemoveStudent;
	private JButton btnRemoveCourseFromStudent;

	public void setAgendaController(AgendaController agendaController) {
		this.agendaController = agendaController;
	}

	public DefaultListModel<Student> getListStudentsModel() {
		return studentsListModel;
	}

	public DefaultListModel<Course> getListCoursesModel() {
		return coursesListModel;
	}

	public DefaultListModel<Course> getListStudentCoursesModel() {
		return studentCoursesListModel;
	}

	public DefaultListModel<Student> getListCourseStudentsModel() {
		return courseStudentsListModel;
	}

	/**
	 * Create the frame.
	 */
	public AgendaSwingView() {
		KeyAdapter addStudentButtonEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnAddNewStudent
				.setEnabled(
						!fieldStudentId.getText().trim().isEmpty() &&
						!fieldStudentName.getText().trim().isEmpty()
						);
			}
		};

		KeyAdapter addCourseButtonEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnAddNewCourse
				.setEnabled(
						!fieldCourseId.getText().trim().isEmpty() &&
						!fieldCourseName.getText().trim().isEmpty() &&
						!fieldCourseCFU.getText().trim().isEmpty() &&
						fieldCourseCFU.getText().trim().matches("^[1-9][0-9]?$|^100$")
						);
			}
		};
		
		setTitle("School Agenda");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 500);
		contentPane = new JPanel();
		contentPane.setName("contentPane");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gblContentPane = new GridBagLayout();
		gblContentPane.columnWidths = new int[]{674, 0};
		gblContentPane.rowHeights = new int[]{401, 0, 0};
		gblContentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gblContentPane.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gblContentPane);

		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.setName("tabbedPane");
		GridBagConstraints gbcTabbedPane = new GridBagConstraints();
		gbcTabbedPane.insets = new Insets(0, 0, 5, 0);
		gbcTabbedPane.fill = GridBagConstraints.BOTH;
		gbcTabbedPane.gridx = 0;
		gbcTabbedPane.gridy = 0;
		contentPane.add(tabbedPane, gbcTabbedPane);

		/*======================================================================
		 *=                                                                    =
		 *=                         Student Panel                              =
		 *=                                                                    =
		 *====================================================================== */

		JPanel studentTab = new JPanel();
		studentTab.setName("studentTab");
		tabbedPane.addTab("Students", null, studentTab, null);
		GridBagLayout gblStudentTab = new GridBagLayout();
		gblStudentTab.rowHeights = new int[] {0, 0, 10, 10, 30, 20, 30, 30, 0, 0, 0, 0, 0, 0, 0, 0};
		gblStudentTab.columnWidths = new int[] {60, 250, 30, 250, 60};
		gblStudentTab.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE, 0.0, 0.0};
		gblStudentTab.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		studentTab.setLayout(gblStudentTab);

		JLabel lblStudentId = new JLabel("ID");
		lblStudentId.setName("studentIDLabel");
		GridBagConstraints gbcLblStudentId = new GridBagConstraints();
		gbcLblStudentId.anchor = GridBagConstraints.EAST;
		gbcLblStudentId.insets = new Insets(0, 0, 5, 5);
		gbcLblStudentId.gridx = 0;
		gbcLblStudentId.gridy = 2;
		studentTab.add(lblStudentId, gbcLblStudentId);

		fieldStudentId = new JTextField();
		fieldStudentId.setName("studentIDTextField");
		fieldStudentId.addKeyListener(addStudentButtonEnabler);
		GridBagConstraints gbcFieldStudentId = new GridBagConstraints();
		gbcFieldStudentId.gridwidth = 3;
		gbcFieldStudentId.insets = new Insets(0, 0, 5, 5);
		gbcFieldStudentId.fill = GridBagConstraints.HORIZONTAL;
		gbcFieldStudentId.gridx = 1;
		gbcFieldStudentId.gridy = 2;
		studentTab.add(fieldStudentId, gbcFieldStudentId);
		fieldStudentId.setColumns(10);

		JLabel lblStudentName = new JLabel("Name");
		lblStudentName.setName("studentNameLabel");
		GridBagConstraints gbcLblStudentName = new GridBagConstraints();
		gbcLblStudentName.anchor = GridBagConstraints.EAST;
		gbcLblStudentName.insets = new Insets(0, 0, 5, 5);
		gbcLblStudentName.gridx = 0;
		gbcLblStudentName.gridy = 3;
		studentTab.add(lblStudentName, gbcLblStudentName);

		fieldStudentName = new JTextField();
		fieldStudentName.setName("studentNameTextField");
		fieldStudentName.addKeyListener(addStudentButtonEnabler);
		GridBagConstraints gbcFieldStudentName = new GridBagConstraints();
		gbcFieldStudentName.gridwidth = 3;
		gbcFieldStudentName.insets = new Insets(0, 0, 5, 5);
		gbcFieldStudentName.fill = GridBagConstraints.HORIZONTAL;
		gbcFieldStudentName.gridx = 1;
		gbcFieldStudentName.gridy = 3;
		studentTab.add(fieldStudentName, gbcFieldStudentName);
		fieldStudentName.setColumns(10);

		btnAddNewStudent = new JButton("Add Student");
		btnAddNewStudent.setEnabled(false);
		btnAddNewStudent.setName("addNewStudentButton");
		btnAddNewStudent.addActionListener(
				e -> agendaController.addStudent(new Student(fieldStudentId.getText(), fieldStudentName.getText()))
				);
		GridBagConstraints gbcBtnAddNewStudent = new GridBagConstraints();
		gbcBtnAddNewStudent.insets = new Insets(0, 0, 5, 5);
		gbcBtnAddNewStudent.gridx = 1;
		gbcBtnAddNewStudent.gridy = 5;
		studentTab.add(btnAddNewStudent, gbcBtnAddNewStudent);

		btnAddCourseToStudent = new JButton("Add Course To Student");
		btnAddCourseToStudent.setEnabled(false);
		btnAddCourseToStudent.addActionListener(
				e -> agendaController.addCourseToStudent(studentsList.getSelectedValue(), coursesList.getSelectedValue()));
		btnAddCourseToStudent.setName("addCourseToStudentButton");
		GridBagConstraints gbcBtnAddCourseToStudent = new GridBagConstraints();
		gbcBtnAddCourseToStudent.insets = new Insets(0, 0, 5, 5);
		gbcBtnAddCourseToStudent.gridx = 3;
		gbcBtnAddCourseToStudent.gridy = 5;
		studentTab.add(btnAddCourseToStudent, gbcBtnAddCourseToStudent);

		JScrollPane scrollPaneStudent = new JScrollPane();
		GridBagConstraints gbcScrollPaneStudent = new GridBagConstraints();
		gbcScrollPaneStudent.gridheight = 6;
		gbcScrollPaneStudent.insets = new Insets(0, 0, 5, 5);
		gbcScrollPaneStudent.fill = GridBagConstraints.BOTH;
		gbcScrollPaneStudent.gridx = 1;
		gbcScrollPaneStudent.gridy = 7;
		studentTab.add(scrollPaneStudent, gbcScrollPaneStudent);

		studentsListModel = new DefaultListModel<>();
		studentsList = new JList<>(studentsListModel);
		studentsList.addListSelectionListener(e -> {
			btnRemoveStudent.setEnabled(studentsList.getSelectedIndex() != -1);
			if (coursesList.getSelectedIndex() != -1) {
				btnAddCourseToStudent.setEnabled(studentsList.getSelectedIndex() != -1 && coursesList.getSelectedIndex() != -1);
				btnAddStudentToCourse.setEnabled(studentsList.getSelectedIndex() != -1 && coursesList.getSelectedIndex() != -1);
			}
		});

		studentsList.setName("studentsList");
		studentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneStudent.setViewportView(studentsList);

		JScrollPane scrollPaneStudentCourses = new JScrollPane();
		GridBagConstraints gbcScrollPaneStudentCourses = new GridBagConstraints();
		gbcScrollPaneStudentCourses.gridheight = 6;
		gbcScrollPaneStudentCourses.insets = new Insets(0, 0, 5, 5);
		gbcScrollPaneStudentCourses.fill = GridBagConstraints.BOTH;
		gbcScrollPaneStudentCourses.gridx = 3;
		gbcScrollPaneStudentCourses.gridy = 7;
		studentTab.add(scrollPaneStudentCourses, gbcScrollPaneStudentCourses);

		studentCoursesListModel = new DefaultListModel<>();
		studentCoursesList = new JList<>(getListStudentCoursesModel());
		studentCoursesList.addListSelectionListener(e -> 
			btnRemoveCourseFromStudent.setEnabled(studentCoursesList.getSelectedIndex() != -1)
		);

		studentCoursesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneStudentCourses.setViewportView(studentCoursesList);

		studentCoursesList.setName("studentCoursesList");
		scrollPaneStudentCourses.setViewportView(studentCoursesList);

		btnRemoveStudent = new JButton("Remove Student");
		btnRemoveStudent.setName("removeStudentButton");
		btnRemoveStudent.setEnabled(false);
		btnRemoveStudent.addActionListener(e -> agendaController.removeStudent(studentsList.getSelectedValue()));
		GridBagConstraints gbcBtnRemoveStudent = new GridBagConstraints();
		gbcBtnRemoveStudent.insets = new Insets(0, 0, 5, 5);
		gbcBtnRemoveStudent.gridx = 1;
		gbcBtnRemoveStudent.gridy = 14;
		studentTab.add(btnRemoveStudent, gbcBtnRemoveStudent);

		btnRemoveCourseFromStudent = new JButton("Remove Course From Student");
		btnRemoveCourseFromStudent.setName("removeCourseFromStudentButton");
		btnRemoveCourseFromStudent.setEnabled(false);
		btnRemoveCourseFromStudent.addActionListener(e -> agendaController.removeCourseFromStudent(studentsList.getSelectedValue(), studentCoursesList.getSelectedValue()));
		GridBagConstraints gbcBtnRemoveCourseFromStudent = new GridBagConstraints();
		gbcBtnRemoveCourseFromStudent.insets = new Insets(0, 0, 5, 5);
		gbcBtnRemoveCourseFromStudent.gridx = 3;
		gbcBtnRemoveCourseFromStudent.gridy = 14;
		studentTab.add(btnRemoveCourseFromStudent, gbcBtnRemoveCourseFromStudent);

		lblStudentMessage = new JLabel("");
		lblStudentMessage.setName("studentMessageLabel");
		GridBagConstraints gbcLblMessage = new GridBagConstraints();
		gbcLblMessage.gridwidth = 3;
		gbcLblMessage.insets = new Insets(0, 0, 0, 5);
		gbcLblMessage.gridx = 1;
		gbcLblMessage.gridy = 15;
		studentTab.add(lblStudentMessage, gbcLblMessage);

		/*======================================================================
		 *=                                                                    =
		 *=                         Course Panel                              =
		 *=                                                                    =
		 *====================================================================== */

		JPanel courseTab = new JPanel();
		courseTab.setName("courseTab");
		tabbedPane.addTab("Courses", null, courseTab, null);
		GridBagLayout gblCourseTab = new GridBagLayout();
		gblCourseTab.columnWidths = new int[]{60, 250, 0, 250, 60};
		gblCourseTab.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gblCourseTab.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		gblCourseTab.rowWeights = new double[]{Double.MIN_VALUE, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		courseTab.setLayout(gblCourseTab);

		JLabel lblCourseId = new JLabel("ID");
		lblCourseId.setName("courseIDLabel");
		GridBagConstraints gbcLblCourseId = new GridBagConstraints();
		gbcLblCourseId.anchor = GridBagConstraints.EAST;
		gbcLblCourseId.insets = new Insets(0, 0, 5, 5);
		gbcLblCourseId.gridx = 0;
		gbcLblCourseId.gridy = 2;
		courseTab.add(lblCourseId, gbcLblCourseId);

		fieldCourseId = new JTextField();
		fieldCourseId.setName("courseIDTextField");
		fieldCourseId.addKeyListener(addCourseButtonEnabler);
		GridBagConstraints gbcFieldCourseId = new GridBagConstraints();
		gbcFieldCourseId.gridwidth = 3;
		gbcFieldCourseId.insets = new Insets(0, 0, 5, 5);
		gbcFieldCourseId.fill = GridBagConstraints.HORIZONTAL;
		gbcFieldCourseId.gridx = 1;
		gbcFieldCourseId.gridy = 2;
		courseTab.add(fieldCourseId, gbcFieldCourseId);
		fieldCourseId.setColumns(10);

		JLabel lblCourseName = new JLabel("Name");
		lblCourseName.setName("courseNameLabel");
		GridBagConstraints gbcLblCourseName = new GridBagConstraints();
		gbcLblCourseName.anchor = GridBagConstraints.EAST;
		gbcLblCourseName.insets = new Insets(0, 0, 5, 5);
		gbcLblCourseName.gridx = 0;
		gbcLblCourseName.gridy = 3;
		courseTab.add(lblCourseName, gbcLblCourseName);

		fieldCourseName = new JTextField();
		fieldCourseName.setName("courseNameTextField");
		fieldCourseName.addKeyListener(addCourseButtonEnabler);
		GridBagConstraints gbcFieldCourseName = new GridBagConstraints();
		gbcFieldCourseName.gridwidth = 3;
		gbcFieldCourseName.insets = new Insets(0, 0, 5, 5);
		gbcFieldCourseName.fill = GridBagConstraints.HORIZONTAL;
		gbcFieldCourseName.gridx = 1;
		gbcFieldCourseName.gridy = 3;
		courseTab.add(fieldCourseName, gbcFieldCourseName);
		fieldCourseName.setColumns(10);

		JLabel lblCourseCFU = new JLabel("CFU");
		lblCourseCFU.setName("courseCFULabel");
		GridBagConstraints gbcLblCourseCFU = new GridBagConstraints();
		gbcLblCourseCFU.anchor = GridBagConstraints.EAST;
		gbcLblCourseCFU.insets = new Insets(0, 0, 5, 5);
		gbcLblCourseCFU.gridx = 0;
		gbcLblCourseCFU.gridy = 4;
		courseTab.add(lblCourseCFU, gbcLblCourseCFU);

		fieldCourseCFU = new JTextField();
		fieldCourseCFU.setName("courseCFUTextField");
		fieldCourseCFU.addKeyListener(addCourseButtonEnabler);
		GridBagConstraints gbcFieldCourseCFU = new GridBagConstraints();
		gbcFieldCourseCFU.gridwidth = 3;
		gbcFieldCourseCFU.insets = new Insets(0, 0, 5, 5);
		gbcFieldCourseCFU.fill = GridBagConstraints.HORIZONTAL;
		gbcFieldCourseCFU.gridx = 1;
		gbcFieldCourseCFU.gridy = 4;
		courseTab.add(fieldCourseCFU, gbcFieldCourseCFU);
		fieldCourseCFU.setColumns(10);

		btnAddNewCourse = new JButton("Add Course");
		btnAddNewCourse.setName("addNewCourseButton");
		btnAddNewCourse.setEnabled(false);
		btnAddNewCourse.addActionListener(e -> agendaController.addCourse(new Course(fieldCourseId.getText(),
				fieldCourseName.getText(), fieldCourseCFU.getText())));
		GridBagConstraints gbcBtnAddNewCourse = new GridBagConstraints();
		gbcBtnAddNewCourse.insets = new Insets(0, 0, 5, 5);
		gbcBtnAddNewCourse.gridx = 1;
		gbcBtnAddNewCourse.gridy = 5;
		courseTab.add(btnAddNewCourse, gbcBtnAddNewCourse);

		btnAddStudentToCourse = new JButton("Add Student To Course");
		btnAddStudentToCourse.setName("addStudentToCourseButton");
		btnAddStudentToCourse.setEnabled(false);
		btnAddStudentToCourse.addActionListener(e -> agendaController.addStudentToCourse(studentsList.getSelectedValue(), coursesList.getSelectedValue()));
		GridBagConstraints gbcBtnAddStudentToCourse = new GridBagConstraints();
		gbcBtnAddStudentToCourse.insets = new Insets(0, 0, 5, 5);
		gbcBtnAddStudentToCourse.gridx = 3;
		gbcBtnAddStudentToCourse.gridy = 5;
		courseTab.add(btnAddStudentToCourse, gbcBtnAddStudentToCourse);

		JScrollPane scrollPaneCourse = new JScrollPane();
		GridBagConstraints gbcScrollPaneCourse = new GridBagConstraints();
		gbcScrollPaneCourse.gridheight = 6;
		gbcScrollPaneCourse.insets = new Insets(0, 0, 5, 5);
		gbcScrollPaneCourse.fill = GridBagConstraints.BOTH;
		gbcScrollPaneCourse.gridx = 1;
		gbcScrollPaneCourse.gridy = 7;
		courseTab.add(scrollPaneCourse, gbcScrollPaneCourse);

		coursesListModel = new DefaultListModel<>();
		coursesList = new JList<>(coursesListModel);
		coursesList.addListSelectionListener(e -> {
			btnRemoveCourse.setEnabled(coursesList.getSelectedIndex() != -1);
			if (studentsList.getSelectedIndex() != -1) {
				btnAddCourseToStudent.setEnabled(studentsList.getSelectedIndex() != -1 && coursesList.getSelectedIndex() != -1);
				btnAddStudentToCourse.setEnabled(studentsList.getSelectedIndex() != -1 && coursesList.getSelectedIndex() != -1);
			}
		});

		coursesList.setName("coursesList");
		coursesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneCourse.setViewportView(coursesList);

		JScrollPane scrollPaneCourseStudents = new JScrollPane();
		GridBagConstraints gbcScrollPaneCourseStudents = new GridBagConstraints();
		gbcScrollPaneCourseStudents.gridheight = 6;
		gbcScrollPaneCourseStudents.insets = new Insets(0, 0, 5, 5);
		gbcScrollPaneCourseStudents.fill = GridBagConstraints.BOTH;
		gbcScrollPaneCourseStudents.gridx = 3;
		gbcScrollPaneCourseStudents.gridy = 7;
		courseTab.add(scrollPaneCourseStudents, gbcScrollPaneCourseStudents);

		courseStudentsListModel = new DefaultListModel<>();
		courseStudentsList = new JList<>(courseStudentsListModel);
		courseStudentsList.addListSelectionListener(e -> 
			btnRemoveStudentFromCourse.setEnabled(courseStudentsList.getSelectedIndex() != -1)
		);

		courseStudentsList.setName("courseStudentsList");
		courseStudentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneCourseStudents.setViewportView(courseStudentsList);

		btnRemoveCourse = new JButton("Remove Course");
		btnRemoveCourse.setName("removeCourseButton");
		btnRemoveCourse.setEnabled(false);
		btnRemoveCourse.addActionListener(e -> agendaController.removeCourse(coursesList.getSelectedValue()));
		GridBagConstraints gbcBtnRemoveCourse = new GridBagConstraints();
		gbcBtnRemoveCourse.insets = new Insets(0, 0, 5, 5);
		gbcBtnRemoveCourse.gridx = 1;
		gbcBtnRemoveCourse.gridy = 14;
		courseTab.add(btnRemoveCourse, gbcBtnRemoveCourse);

		btnRemoveStudentFromCourse = new JButton("Remove Student From Course");
		btnRemoveStudentFromCourse.setName("removeStudentFromCourseButton");
		btnRemoveStudentFromCourse.setEnabled(false);
		btnRemoveStudentFromCourse.addActionListener(e -> agendaController.removeStudentFromCourse(courseStudentsList.getSelectedValue(), coursesList.getSelectedValue()));
		GridBagConstraints gbcBtnRemoveStudentFromCourse = new GridBagConstraints();
		gbcBtnRemoveStudentFromCourse.insets = new Insets(0, 0, 5, 5);
		gbcBtnRemoveStudentFromCourse.gridx = 3;
		gbcBtnRemoveStudentFromCourse.gridy = 14;
		courseTab.add(btnRemoveStudentFromCourse, gbcBtnRemoveStudentFromCourse);

		lblCourseMessage = new JLabel("");
		lblCourseMessage.setName("courseMessageLabel");
		GridBagConstraints gbcLblCourseMessage = new GridBagConstraints();
		gbcLblCourseMessage.gridwidth = 3;
		gbcLblCourseMessage.insets = new Insets(0, 0, 0, 5);
		gbcLblCourseMessage.gridx = 1;
		gbcLblCourseMessage.gridy = 15;
		courseTab.add(lblCourseMessage, gbcLblCourseMessage);
	}

	@Override
	public void showAllStudents(List<Student> allStudents) {
		allStudents.stream().forEach(studentsListModel::addElement); 
	}

	@Override
	public void notifyStudentAdded(Student student) {
		studentsListModel.addElement(student);
		lblStudentMessage.setText(student.toString() + " successfully added!");
	}

	@Override
	public void notifyStudentNotAdded(Student student) {
		lblStudentMessage.setText(ERROR + student.toString() + " NOT added!");
	}

	@Override
	public void notifyStudentRemoved(Student student) {
		studentsListModel.removeElement(student);
		lblStudentMessage.setText(student.toString() + " successfully removed!");
	}

	@Override
	public void notifyStudentNotRemoved(Student student) {
		lblStudentMessage.setText(ERROR + student.toString() + " NOT removed!");
	}

	@Override
	public void notifyCourseAddedToStudent(Student student, Course course) {
		studentCoursesListModel.addElement(course);
		lblStudentMessage.setText(course.toString() + " added to " + student.toString());
	}

	@Override
	public void notifyCourseNotAddedToStudent(Student student, Course course) {
		lblStudentMessage.setText(ERROR + course.toString() + " NOT added to " + student.toString());
	}

	@Override
	public void notifyCourseRemovedFromStudent(Student student, Course course) {
		studentCoursesListModel.removeElement(course);
		lblStudentMessage.setText(course.toString() + " removed from " + student.toString());
	}

	@Override
	public void notifyCourseNotRemovedFromStudent(Student student, Course course) {
		lblStudentMessage.setText(ERROR + course.toString() + " NOT removed from " + student.toString());
	}

	@Override
	public void notifyCourseAdded(Course course) {
		coursesListModel.addElement(course);
		lblCourseMessage.setText(course.toString() + " successfully added!");
	}

	@Override
	public void notifyCourseNotAdded(Course course) {
		lblCourseMessage.setText(ERROR + course.toString() + " NOT added!");
	}

	@Override
	public void notifyCourseRemoved(Course course) {
		coursesListModel.removeElement(course);
		lblCourseMessage.setText(course.toString() + " successfully removed!");
	}

	@Override
	public void notifyCourseNotRemoved(Course course) {
		lblCourseMessage.setText(ERROR + course.toString() + " NOT removed!");
	}

	@Override
	public void notifyStudentRemovedFromCourse(Student student, Course course) {
		courseStudentsListModel.removeElement(student);
		lblCourseMessage.setText(student.toString() + " removed from " + course.toString());
	}

	@Override
	public void notifyStudentNotRemovedFromCourse(Student student, Course course) {
		lblCourseMessage.setText(ERROR + student.toString() + " NOT removed from " + course.toString());
	}

	@Override
	public void notifyStudentNotAddedToCourse(Student student, Course course) {
		lblCourseMessage.setText(ERROR + student.toString() + " NOT added to " + course.toString());
	}

	@Override
	public void notifyStudentAddedToCourse(Student student, Course course) {
		courseStudentsListModel.addElement(student);
		lblCourseMessage.setText(student.toString() + " added to " + course.toString());
	}

	@Override
	public void showAllCourses(List<Course> allCourses) {
		allCourses.stream().forEach(coursesListModel::addElement); 
	}

	@Override
	public void showAllStudentCourses(List<Course> studentCourses) {
		studentCourses.stream().forEach(studentCoursesListModel::addElement);
	}

	@Override
	public void showAllCourseStudents(List<Student> courseStudents) {
		courseStudents.stream().forEach(courseStudentsListModel::addElement);    
	}
}
