package view.swing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controller.AgendaController;
import model.Course;
import model.Student;
import view.AgendaView;

import java.awt.GridBagLayout;
import javax.swing.JTabbedPane;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JList;

public class AgendaSwingView extends JFrame implements AgendaView {

	private AgendaController agendaController;

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
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AgendaSwingView frame = new AgendaSwingView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 500);
		contentPane = new JPanel();
		contentPane.setName("contentPane");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{674, 0};
		gbl_contentPane.rowHeights = new int[]{401, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setName("tabbedPane");
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		contentPane.add(tabbedPane, gbc_tabbedPane);

		/*======================================================================
		 *=                                                                    =
		 *=                         Student Panel                              =
		 *=                                                                    =
		 *====================================================================== */

		JPanel studentTab = new JPanel();
		studentTab.setName("studentTab");
		tabbedPane.addTab("Students", null, studentTab, null);
		GridBagLayout gbl_studentTab = new GridBagLayout();
		gbl_studentTab.rowHeights = new int[] {0, 0, 10, 10, 30, 20, 30, 30, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_studentTab.columnWidths = new int[] {60, 250, 30, 250, 60};
		gbl_studentTab.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE, 0.0, 0.0};
		gbl_studentTab.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		studentTab.setLayout(gbl_studentTab);

		JLabel lblStudentId = new JLabel("ID");
		lblStudentId.setName("studentIDLabel");
		GridBagConstraints gbc_lblStudentId = new GridBagConstraints();
		gbc_lblStudentId.anchor = GridBagConstraints.EAST;
		gbc_lblStudentId.insets = new Insets(0, 0, 5, 5);
		gbc_lblStudentId.gridx = 0;
		gbc_lblStudentId.gridy = 2;
		studentTab.add(lblStudentId, gbc_lblStudentId);

		fieldStudentId = new JTextField();
		fieldStudentId.setName("studentIDTextField");
		fieldStudentId.addKeyListener(addStudentButtonEnabler);
		GridBagConstraints gbc_fieldStudentId = new GridBagConstraints();
		gbc_fieldStudentId.gridwidth = 3;
		gbc_fieldStudentId.insets = new Insets(0, 0, 5, 5);
		gbc_fieldStudentId.fill = GridBagConstraints.HORIZONTAL;
		gbc_fieldStudentId.gridx = 1;
		gbc_fieldStudentId.gridy = 2;
		studentTab.add(fieldStudentId, gbc_fieldStudentId);
		fieldStudentId.setColumns(10);

		JLabel lblStudentName = new JLabel("Name");
		lblStudentName.setName("studentNameLabel");
		GridBagConstraints gbc_lblStudentName = new GridBagConstraints();
		gbc_lblStudentName.anchor = GridBagConstraints.EAST;
		gbc_lblStudentName.insets = new Insets(0, 0, 5, 5);
		gbc_lblStudentName.gridx = 0;
		gbc_lblStudentName.gridy = 3;
		studentTab.add(lblStudentName, gbc_lblStudentName);

		fieldStudentName = new JTextField();
		fieldStudentName.setName("studentNameTextField");
		fieldStudentName.addKeyListener(addStudentButtonEnabler);
		GridBagConstraints gbc_fieldStudentName = new GridBagConstraints();
		gbc_fieldStudentName.gridwidth = 3;
		gbc_fieldStudentName.insets = new Insets(0, 0, 5, 5);
		gbc_fieldStudentName.fill = GridBagConstraints.HORIZONTAL;
		gbc_fieldStudentName.gridx = 1;
		gbc_fieldStudentName.gridy = 3;
		studentTab.add(fieldStudentName, gbc_fieldStudentName);
		fieldStudentName.setColumns(10);

		btnAddNewStudent = new JButton("Add Student");
		btnAddNewStudent.setEnabled(false);
		btnAddNewStudent.setName("addNewStudentButton");
		btnAddNewStudent.addActionListener(
				e -> agendaController.addStudent(new Student(fieldStudentId.getText(), fieldStudentName.getText()))
				);
		GridBagConstraints gbc_btnAddNewStudent = new GridBagConstraints();
		gbc_btnAddNewStudent.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddNewStudent.gridx = 1;
		gbc_btnAddNewStudent.gridy = 5;
		studentTab.add(btnAddNewStudent, gbc_btnAddNewStudent);

		btnAddCourseToStudent = new JButton("Add Course To Student");
		btnAddCourseToStudent.setEnabled(false);
		btnAddCourseToStudent.addActionListener(
				e -> agendaController.addCourseToStudent(studentsList.getSelectedValue(), coursesList.getSelectedValue()));
		btnAddCourseToStudent.setName("addCourseToStudentButton");
		GridBagConstraints gbc_btnAddCourseToStudent = new GridBagConstraints();
		gbc_btnAddCourseToStudent.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddCourseToStudent.gridx = 3;
		gbc_btnAddCourseToStudent.gridy = 5;
		studentTab.add(btnAddCourseToStudent, gbc_btnAddCourseToStudent);

		JScrollPane scrollPaneStudent = new JScrollPane();
		GridBagConstraints gbc_scrollPaneStudent = new GridBagConstraints();
		gbc_scrollPaneStudent.gridheight = 6;
		gbc_scrollPaneStudent.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneStudent.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneStudent.gridx = 1;
		gbc_scrollPaneStudent.gridy = 7;
		studentTab.add(scrollPaneStudent, gbc_scrollPaneStudent);

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
		GridBagConstraints gbc_scrollPaneStudentCourses = new GridBagConstraints();
		gbc_scrollPaneStudentCourses.gridheight = 6;
		gbc_scrollPaneStudentCourses.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneStudentCourses.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneStudentCourses.gridx = 3;
		gbc_scrollPaneStudentCourses.gridy = 7;
		studentTab.add(scrollPaneStudentCourses, gbc_scrollPaneStudentCourses);

		studentCoursesListModel = new DefaultListModel<>();
		studentCoursesList = new JList<>(getListStudentCoursesModel());
		studentCoursesList.addListSelectionListener(e -> {
			btnRemoveCourseFromStudent.setEnabled(studentCoursesList.getSelectedIndex() != -1);
		});

		studentCoursesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneStudentCourses.setViewportView(studentCoursesList);

		studentCoursesList.setName("studentCoursesList");
		scrollPaneStudentCourses.setViewportView(studentCoursesList);

		btnRemoveStudent = new JButton("Remove Student");
		btnRemoveStudent.setName("removeStudentButton");
		btnRemoveStudent.setEnabled(false);
		btnRemoveStudent.addActionListener(e -> agendaController.removeStudent(studentsList.getSelectedValue()));
		GridBagConstraints gbc_btnRemoveStudent = new GridBagConstraints();
		gbc_btnRemoveStudent.insets = new Insets(0, 0, 5, 5);
		gbc_btnRemoveStudent.gridx = 1;
		gbc_btnRemoveStudent.gridy = 14;
		studentTab.add(btnRemoveStudent, gbc_btnRemoveStudent);

		btnRemoveCourseFromStudent = new JButton("Remove Course From Student");
		btnRemoveCourseFromStudent.setName("removeCourseFromStudentButton");
		btnRemoveCourseFromStudent.setEnabled(false);
		btnRemoveCourseFromStudent.addActionListener(e -> agendaController.removeCourseFromStudent(studentsList.getSelectedValue(), studentCoursesList.getSelectedValue()));
		GridBagConstraints gbc_btnRemoveCourseFromStudent = new GridBagConstraints();
		gbc_btnRemoveCourseFromStudent.insets = new Insets(0, 0, 5, 5);
		gbc_btnRemoveCourseFromStudent.gridx = 3;
		gbc_btnRemoveCourseFromStudent.gridy = 14;
		studentTab.add(btnRemoveCourseFromStudent, gbc_btnRemoveCourseFromStudent);

		lblStudentMessage = new JLabel("");
		lblStudentMessage.setName("studentMessageLabel");
		GridBagConstraints gbc_lblMessage = new GridBagConstraints();
		gbc_lblMessage.gridwidth = 3;
		gbc_lblMessage.insets = new Insets(0, 0, 0, 5);
		gbc_lblMessage.gridx = 1;
		gbc_lblMessage.gridy = 15;
		studentTab.add(lblStudentMessage, gbc_lblMessage);

		/*======================================================================
		 *=                                                                    =
		 *=                         Course Panel                              =
		 *=                                                                    =
		 *====================================================================== */

		JPanel courseTab = new JPanel();
		courseTab.setName("courseTab");
		tabbedPane.addTab("Courses", null, courseTab, null);
		GridBagLayout gbl_courseTab = new GridBagLayout();
		gbl_courseTab.columnWidths = new int[]{60, 250, 0, 250, 60};
		gbl_courseTab.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_courseTab.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_courseTab.rowWeights = new double[]{Double.MIN_VALUE, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		courseTab.setLayout(gbl_courseTab);

		JLabel lblCourseId = new JLabel("ID");
		lblCourseId.setName("courseIDLabel");
		GridBagConstraints gbc_lblCourseId = new GridBagConstraints();
		gbc_lblCourseId.anchor = GridBagConstraints.EAST;
		gbc_lblCourseId.insets = new Insets(0, 0, 5, 5);
		gbc_lblCourseId.gridx = 0;
		gbc_lblCourseId.gridy = 2;
		courseTab.add(lblCourseId, gbc_lblCourseId);

		fieldCourseId = new JTextField();
		fieldCourseId.setName("courseIDTextField");
		fieldCourseId.addKeyListener(addCourseButtonEnabler);
		GridBagConstraints gbc_fieldCourseId = new GridBagConstraints();
		gbc_fieldCourseId.gridwidth = 3;
		gbc_fieldCourseId.insets = new Insets(0, 0, 5, 5);
		gbc_fieldCourseId.fill = GridBagConstraints.HORIZONTAL;
		gbc_fieldCourseId.gridx = 1;
		gbc_fieldCourseId.gridy = 2;
		courseTab.add(fieldCourseId, gbc_fieldCourseId);
		fieldCourseId.setColumns(10);

		JLabel lblCourseName = new JLabel("Name");
		lblCourseName.setName("courseNameLabel");
		GridBagConstraints gbc_lblCourseName = new GridBagConstraints();
		gbc_lblCourseName.anchor = GridBagConstraints.EAST;
		gbc_lblCourseName.insets = new Insets(0, 0, 5, 5);
		gbc_lblCourseName.gridx = 0;
		gbc_lblCourseName.gridy = 3;
		courseTab.add(lblCourseName, gbc_lblCourseName);

		fieldCourseName = new JTextField();
		fieldCourseName.setName("courseNameTextField");
		fieldCourseName.addKeyListener(addCourseButtonEnabler);
		GridBagConstraints gbc_fieldCourseName = new GridBagConstraints();
		gbc_fieldCourseName.gridwidth = 3;
		gbc_fieldCourseName.insets = new Insets(0, 0, 5, 5);
		gbc_fieldCourseName.fill = GridBagConstraints.HORIZONTAL;
		gbc_fieldCourseName.gridx = 1;
		gbc_fieldCourseName.gridy = 3;
		courseTab.add(fieldCourseName, gbc_fieldCourseName);
		fieldCourseName.setColumns(10);

		JLabel lblCourseCFU = new JLabel("CFU");
		lblCourseCFU.setName("courseCFULabel");
		GridBagConstraints gbc_lblCourseCFU = new GridBagConstraints();
		gbc_lblCourseCFU.anchor = GridBagConstraints.EAST;
		gbc_lblCourseCFU.insets = new Insets(0, 0, 5, 5);
		gbc_lblCourseCFU.gridx = 0;
		gbc_lblCourseCFU.gridy = 4;
		courseTab.add(lblCourseCFU, gbc_lblCourseCFU);

		fieldCourseCFU = new JTextField();
		fieldCourseCFU.setName("courseCFUTextField");
		fieldCourseCFU.addKeyListener(addCourseButtonEnabler);
		GridBagConstraints gbc_fieldCourseCFU = new GridBagConstraints();
		gbc_fieldCourseCFU.gridwidth = 3;
		gbc_fieldCourseCFU.insets = new Insets(0, 0, 5, 5);
		gbc_fieldCourseCFU.fill = GridBagConstraints.HORIZONTAL;
		gbc_fieldCourseCFU.gridx = 1;
		gbc_fieldCourseCFU.gridy = 4;
		courseTab.add(fieldCourseCFU, gbc_fieldCourseCFU);
		fieldCourseCFU.setColumns(10);

		btnAddNewCourse = new JButton("Add Course");
		btnAddNewCourse.setName("addNewCourseButton");
		btnAddNewCourse.setEnabled(false);
		btnAddNewCourse.addActionListener(e -> agendaController.addCourse(new Course(fieldCourseId.getText(),
				fieldCourseName.getText(), fieldCourseCFU.getText())));
		GridBagConstraints gbc_btnAddNewCourse = new GridBagConstraints();
		gbc_btnAddNewCourse.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddNewCourse.gridx = 1;
		gbc_btnAddNewCourse.gridy = 5;
		courseTab.add(btnAddNewCourse, gbc_btnAddNewCourse);

		btnAddStudentToCourse = new JButton("Add Student To Course");
		btnAddStudentToCourse.setName("addStudentToCourseButton");
		btnAddStudentToCourse.setEnabled(false);
		btnAddStudentToCourse.addActionListener(e -> agendaController.addStudentToCourse(studentsList.getSelectedValue(), coursesList.getSelectedValue()));
		GridBagConstraints gbc_btnAddStudentToCourse = new GridBagConstraints();
		gbc_btnAddStudentToCourse.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddStudentToCourse.gridx = 3;
		gbc_btnAddStudentToCourse.gridy = 5;
		courseTab.add(btnAddStudentToCourse, gbc_btnAddStudentToCourse);

		JScrollPane scrollPaneCourse = new JScrollPane();
		GridBagConstraints gbc_scrollPaneCourse = new GridBagConstraints();
		gbc_scrollPaneCourse.gridheight = 6;
		gbc_scrollPaneCourse.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneCourse.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneCourse.gridx = 1;
		gbc_scrollPaneCourse.gridy = 7;
		courseTab.add(scrollPaneCourse, gbc_scrollPaneCourse);

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
		GridBagConstraints gbc_scrollPaneCourseStudents = new GridBagConstraints();
		gbc_scrollPaneCourseStudents.gridheight = 6;
		gbc_scrollPaneCourseStudents.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneCourseStudents.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneCourseStudents.gridx = 3;
		gbc_scrollPaneCourseStudents.gridy = 7;
		courseTab.add(scrollPaneCourseStudents, gbc_scrollPaneCourseStudents);

		courseStudentsListModel = new DefaultListModel<>();
		courseStudentsList = new JList<>(courseStudentsListModel);
		courseStudentsList.addListSelectionListener(e -> {
			btnRemoveStudentFromCourse.setEnabled(courseStudentsList.getSelectedIndex() != -1);
		});

		courseStudentsList.setName("courseStudentsList");
		courseStudentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneCourseStudents.setViewportView(courseStudentsList);

		btnRemoveCourse = new JButton("Remove Course");
		btnRemoveCourse.setName("removeCourseButton");
		btnRemoveCourse.setEnabled(false);
		btnRemoveCourse.addActionListener(e -> agendaController.removeCourse(coursesList.getSelectedValue()));
		GridBagConstraints gbc_btnRemoveCourse = new GridBagConstraints();
		gbc_btnRemoveCourse.insets = new Insets(0, 0, 5, 5);
		gbc_btnRemoveCourse.gridx = 1;
		gbc_btnRemoveCourse.gridy = 14;
		courseTab.add(btnRemoveCourse, gbc_btnRemoveCourse);

		btnRemoveStudentFromCourse = new JButton("Remove Student From Course");
		btnRemoveStudentFromCourse.setName("removeStudentFromCourseButton");
		btnRemoveStudentFromCourse.setEnabled(false);
		btnRemoveStudentFromCourse.addActionListener(e -> agendaController.removeStudentFromCourse(courseStudentsList.getSelectedValue(), coursesList.getSelectedValue()));
		GridBagConstraints gbc_btnRemoveStudentFromCourse = new GridBagConstraints();
		gbc_btnRemoveStudentFromCourse.insets = new Insets(0, 0, 5, 5);
		gbc_btnRemoveStudentFromCourse.gridx = 3;
		gbc_btnRemoveStudentFromCourse.gridy = 14;
		courseTab.add(btnRemoveStudentFromCourse, gbc_btnRemoveStudentFromCourse);

		lblCourseMessage = new JLabel("");
		lblCourseMessage.setName("courseMessageLabel");
		GridBagConstraints gbc_lblCourseMessage = new GridBagConstraints();
		gbc_lblCourseMessage.gridwidth = 3;
		gbc_lblCourseMessage.insets = new Insets(0, 0, 0, 5);
		gbc_lblCourseMessage.gridx = 1;
		gbc_lblCourseMessage.gridy = 15;
		courseTab.add(lblCourseMessage, gbc_lblCourseMessage);
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
		lblStudentMessage.setText("ERROR! " + student.toString() + " NOT added!");
	}

	@Override
	public void notifyStudentRemoved(Student student) {
		studentsListModel.removeElement(student);
		lblStudentMessage.setText(student.toString() + " successfully removed!");
	}

	@Override
	public void notifyStudentNotRemoved(Student student) {
		lblStudentMessage.setText("ERROR! " + student.toString() + " NOT removed!");
	}

	@Override
	public void notifyCourseAddedToStudent(Student student, Course course) {
		studentCoursesListModel.addElement(course);
		lblStudentMessage.setText(course.toString() + " added to " + student.toString());
	}

	@Override
	public void notifyCourseNotAddedToStudent(Student student, Course course) {
		lblStudentMessage.setText("ERROR! " + course.toString() + " NOT added to " + student.toString());
	}

	@Override
	public void notifyCourseRemovedFromStudent(Student student, Course course) {
		studentCoursesListModel.removeElement(course);
		lblStudentMessage.setText(course.toString() + " removed from " + student.toString());
	}

	@Override
	public void notifyCourseNotRemovedFromStudent(Student student, Course course) {
		lblStudentMessage.setText("ERROR! " + course.toString() + " NOT removed from " + student.toString());
	}

	@Override
	public void notifyCourseAdded(Course course) {
		coursesListModel.addElement(course);
		lblCourseMessage.setText(course.toString() + " successfully added!");
	}

	@Override
	public void notifyCourseNotAdded(Course course) {
		lblCourseMessage.setText("ERROR! " + course.toString() + " NOT added!");
	}

	@Override
	public void notifyCourseRemoved(Course course) {
		coursesListModel.removeElement(course);
		lblCourseMessage.setText(course.toString() + " successfully removed!");
	}

	@Override
	public void notifyCourseNotRemoved(Course course) {
		lblCourseMessage.setText("ERROR! " + course.toString() + " NOT removed!");
	}

	@Override
	public void notifyStudentRemovedFromCourse(Student student, Course course) {
		courseStudentsListModel.removeElement(student);
		lblCourseMessage.setText(student.toString() + " removed from " + course.toString());
	}

	@Override
	public void notifyStudentNotRemovedFromCourse(Student student, Course course) {
		lblCourseMessage.setText("ERROR! " + student.toString() + " NOT removed from " + course.toString());
	}

	@Override
	public void notifyStudentNotAddedToCourse(Student student, Course course) {
		lblCourseMessage.setText("ERROR! " + student.toString() + " NOT added to " + course.toString());
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
