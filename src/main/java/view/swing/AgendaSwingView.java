package view.swing;

import java.awt.EventQueue;
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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import controller.AgendaController;
import model.Course;
import model.Student;
import view.AgendaView;
import javax.swing.UIManager;

public class AgendaSwingView extends JFrame implements AgendaView {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField fieldStudentId;
	private JTextField fieldStudentName;
	private JTextField fieldCourseId;
	private JTextField fieldCourseName;
	private JTextField fieldCourseCFU;
	private AgendaController agendaController;
	private JPanel studentPanel;
	private JLabel lblStudent;
	private JLabel lblStudentId;
	private JLabel lblStudentName;
	private JButton btnAddNewStudent;
	private JButton btnAddStudentToCourse;
	private JButton btnRemoveStudent;
	private JButton btnRemoveCourseFromStudent;
	private JList<Student> studentsList;
	private JList<Course> studentCoursesList;
	private JScrollPane scrollPaneCourses;
	private JScrollPane scrollPaneCourseStudents;
	private JLabel lblStudentErrorNotAddedMessage;
	private JLabel lblStudentAddedMessage;
	private JLabel lblStudentErrorNotRemovedMessage;
	private JLabel lblStudentRemovedMessage;
	private JLabel lblCourseErrorNotAddedToStudentMessage;
	private JLabel lblCourseAddedToStudentMessage;
	private JLabel lblCourseErrorNotRemovedFromStudentMessage;
	private JLabel lblCourseRemovedFromStudentMessage;
	private JPanel coursePanel;
	private JLabel lblCourse;
	private JLabel lblCourseId;
	private JLabel lblCourseName;
	private JLabel lblCfu;
	private JButton btnAddNewCourse;
	private JButton btnAddCourseToStudent;
	private JButton btnRemoveCourse;
	private JButton btnRemoveStudentFromCourse;
	private JList<Course> coursesList;
	private JList<Student> courseStudentsList;
	private JScrollPane scrollPaneStudents;
	private JScrollPane scrollPaneStudentCourses;
	private JLabel lblCourseErrorNotAddedMessage;
	private JLabel lblCourseAddedMessage;
	private JLabel lblCourseErrorNotRemovedMessage;
	private JLabel lblCourseRemovedMessage;
	private JLabel lblStudentErrorNotRemovedFromCourseMessage;
	private JLabel lblStudentRemovedFromCourseMessage;
	private JLabel lblStudentErrorNotAddedToCourseMessage;
	private JLabel lblStudentAddedToCourseMessage;

	private DefaultListModel<Student> studentsListModel;
	private DefaultListModel<Course> studentCoursesListModel;
	private DefaultListModel<Course> coursesListModel;
	private DefaultListModel<Student> courseStudentsListModel;

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

		setTitle("School Agenda");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 900);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		contentPane.setLayout(gbl_contentPane);

		/* ===================================================================================================
		 * =                                                                                                 =   
		 * =                                       Student Panel                                             =
		 * =                                                                                                 =
		 * ===================================================================================================*/

		studentPanel = new JPanel();
		GridBagConstraints gbc_studentPanel = new GridBagConstraints();
		gbc_studentPanel.fill = GridBagConstraints.BOTH;
		gbc_studentPanel.insets = new Insets(0, 0, 0, 5);
		gbc_studentPanel.gridx = 0;
		gbc_studentPanel.gridy = 0;
		contentPane.add(studentPanel, gbc_studentPanel);
		GridBagLayout gbl_studentPanel = new GridBagLayout();
		gbl_studentPanel.columnWidths = new int[] { 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30 };
		gbl_studentPanel.rowHeights = new int[] { 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30 };
		studentPanel.setLayout(gbl_studentPanel);

		lblStudent = new JLabel("Student");
		lblStudent.setName("studentPanel");
		GridBagConstraints gbc_lblStudent = new GridBagConstraints();
		gbc_lblStudent.gridx = 0;
		gbc_lblStudent.gridy = 0;
		gbc_lblStudent.gridwidth = 11;
		gbc_lblStudent.insets = new Insets(0, 0, 5, 0);
		studentPanel.add(lblStudent, gbc_lblStudent);

		lblStudentId = new JLabel("ID");
		lblStudentId.setName("studentIDLabel");
		GridBagConstraints gbc_lblStudentId = new GridBagConstraints();
		gbc_lblStudentId.gridx = 1;
		gbc_lblStudentId.gridy = 2;
		gbc_lblStudentId.gridwidth = 3;
		gbc_lblStudentId.anchor = GridBagConstraints.EAST;
		gbc_lblStudentId.insets = new Insets(0, 0, 5, 5);
		studentPanel.add(lblStudentId, gbc_lblStudentId);

		fieldStudentId = new JTextField();
		fieldStudentId.addKeyListener(addStudentButtonEnabler);
		fieldStudentId.setName("studentIDTextField");
		GridBagConstraints gbc_fieldStudentId = new GridBagConstraints();
		gbc_fieldStudentId.gridx = 4;
		gbc_fieldStudentId.gridy = 2;
		gbc_fieldStudentId.gridwidth = 7;
		gbc_fieldStudentId.insets = new Insets(0, 0, 5, 0);
		gbc_fieldStudentId.fill = GridBagConstraints.HORIZONTAL;
		studentPanel.add(fieldStudentId, gbc_fieldStudentId);
		fieldStudentId.setColumns(7);

		lblStudentName = new JLabel("Name");
		lblStudentName.setName("studentNameLabel");
		GridBagConstraints gbc_lblStudentName = new GridBagConstraints();
		gbc_lblStudentName.gridx = 1;
		gbc_lblStudentName.gridy = 3;
		gbc_lblStudentName.gridwidth = 3;
		gbc_lblStudentName.anchor = GridBagConstraints.EAST;
		gbc_lblStudentName.insets = new Insets(0, 0, 5, 5);
		studentPanel.add(lblStudentName, gbc_lblStudentName);

		fieldStudentName = new JTextField();
		fieldStudentName.addKeyListener(addStudentButtonEnabler);
		fieldStudentName.setName("studentNameTextField");
		GridBagConstraints gbc_fieldStudentName = new GridBagConstraints();
		gbc_fieldStudentName.gridx = 4;
		gbc_fieldStudentName.gridy = 3;
		gbc_fieldStudentName.gridwidth = 7;
		gbc_fieldStudentName.insets = new Insets(0, 0, 5, 0);
		gbc_fieldStudentName.fill = GridBagConstraints.HORIZONTAL;
		studentPanel.add(fieldStudentName, gbc_fieldStudentName);
		fieldStudentName.setColumns(7);

		btnAddNewStudent = new JButton("Add new student");
		btnAddNewStudent.setName("addNewStudentButton");
		btnAddNewStudent.setEnabled(false);
		btnAddNewStudent.addActionListener(
				e -> agendaController.addStudent(new Student(fieldStudentId.getText(), fieldStudentName.getText()))
				);
		GridBagConstraints gbc_btnAddNewStudent = new GridBagConstraints();
		gbc_btnAddNewStudent.gridx = 1;
		gbc_btnAddNewStudent.gridy = 6;
		gbc_btnAddNewStudent.gridwidth = 4;
		gbc_btnAddNewStudent.insets = new Insets(0, 0, 5, 0);
		studentPanel.add(btnAddNewStudent, gbc_btnAddNewStudent);

		btnAddCourseToStudent = new JButton("Add to student");
		btnAddCourseToStudent.setName("addCourseToStudentButton");
		btnAddCourseToStudent.setEnabled(false);
		btnAddCourseToStudent.addActionListener(
				e -> agendaController.addCourseToStudent(studentsList.getSelectedValue(), coursesList.getSelectedValue()));
		GridBagConstraints gbc_btnAddCourseToStudent = new GridBagConstraints();
		gbc_btnAddCourseToStudent.gridx = 7;
		gbc_btnAddCourseToStudent.gridy = 6;
		gbc_btnAddCourseToStudent.gridwidth = 4; 
		gbc_btnAddCourseToStudent.insets = new Insets(0, 0, 5, 0);
		studentPanel.add(btnAddCourseToStudent, gbc_btnAddCourseToStudent);

		scrollPaneStudents = new JScrollPane();
		GridBagConstraints gbc_scrollPaneStudents = new GridBagConstraints();
		gbc_scrollPaneStudents.gridx = 1;
		gbc_scrollPaneStudents.gridy = 7;
		gbc_scrollPaneStudents.gridwidth = 4;
		gbc_scrollPaneStudents.gridheight = 4;
		gbc_scrollPaneStudents.insets = new Insets(2, 0, 2, 0);
		gbc_scrollPaneStudents.fill = GridBagConstraints.BOTH;
		studentPanel.add(scrollPaneStudents, gbc_scrollPaneStudents);

		studentsListModel = new DefaultListModel<>();
		studentsList = new JList<>(studentsListModel);
		// FIXME
		studentsList.addListSelectionListener(e -> {
			btnRemoveStudent.setEnabled(studentsList.getSelectedIndex() != -1);
			if (coursesList.getSelectedIndex() != -1) {
				btnAddCourseToStudent.setEnabled(studentsList.getSelectedIndex() != -1 && coursesList.getSelectedIndex() != -1);
				btnAddStudentToCourse.setEnabled(studentsList.getSelectedIndex() != -1 && coursesList.getSelectedIndex() != -1);
			}
		});
		studentsList.setName("studentsList");
		studentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneStudents.setViewportView(studentsList);

		scrollPaneStudentCourses = new JScrollPane();
		GridBagConstraints gbc_scrollPaneStudentCourses = new GridBagConstraints();
		gbc_scrollPaneStudentCourses.gridx = 7;
		gbc_scrollPaneStudentCourses.gridy = 7;
		gbc_scrollPaneStudentCourses.gridwidth = 4;
		gbc_scrollPaneStudentCourses.gridheight = 4;
		gbc_scrollPaneStudentCourses.insets = new Insets(2, 0, 2, 0);
		gbc_scrollPaneStudentCourses.fill = GridBagConstraints.BOTH;
		studentPanel.add(scrollPaneStudentCourses, gbc_scrollPaneStudentCourses);

		studentCoursesListModel = new DefaultListModel<>();
		studentCoursesList = new JList<>(getListStudentCoursesModel());
		// FIXME
		studentCoursesList.addListSelectionListener(e -> {
			btnRemoveCourseFromStudent.setEnabled(studentCoursesList.getSelectedIndex() != -1);
		});
		studentCoursesList.setName("studentCoursesList");
		studentCoursesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneStudentCourses.setViewportView(studentCoursesList);

		btnRemoveStudent = new JButton("Remove student");
		btnRemoveStudent.setName("removeStudentButton");
		btnRemoveStudent.setEnabled(false);
		btnRemoveStudent.addActionListener(e -> agendaController.removeStudent(studentsList.getSelectedValue()));
		GridBagConstraints gbc_btnRemoveStudent = new GridBagConstraints();
		gbc_btnRemoveStudent.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemoveStudent.gridx = 1;
		gbc_btnRemoveStudent.gridy = 11;
		gbc_btnRemoveStudent.gridwidth = 4;
		studentPanel.add(btnRemoveStudent, gbc_btnRemoveStudent);

		btnRemoveCourseFromStudent = new JButton("Remove from student");
		btnRemoveCourseFromStudent.setName("removeCourseFromStudentButton");
		btnRemoveCourseFromStudent.setEnabled(false);
		btnRemoveCourseFromStudent.addActionListener(e -> agendaController.removeCourseFromStudent(studentsList.getSelectedValue(), studentCoursesList.getSelectedValue()));
		GridBagConstraints gbc_btnRemoveCourseFromStudent = new GridBagConstraints();
		gbc_btnRemoveCourseFromStudent.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemoveCourseFromStudent.gridx = 7;
		gbc_btnRemoveCourseFromStudent.gridy = 11;
		gbc_btnRemoveCourseFromStudent.gridwidth = 4;
		studentPanel.add(btnRemoveCourseFromStudent, gbc_btnRemoveCourseFromStudent);

		/** Student error not added label */
		lblStudentErrorNotAddedMessage = new JLabel("");
		lblStudentErrorNotAddedMessage.setName("studentErrorNotAddedLabel");
		GridBagConstraints gbc_lblStudentErrorNotAddedMessage = new GridBagConstraints();
		gbc_lblStudentErrorNotAddedMessage.insets = new Insets(0, 0, 5, 0);
		gbc_lblStudentErrorNotAddedMessage.gridwidth = 2;
		gbc_lblStudentErrorNotAddedMessage.gridx = 0;
		gbc_lblStudentErrorNotAddedMessage.gridy = 12;
		studentPanel.add(lblStudentErrorNotAddedMessage, gbc_lblStudentErrorNotAddedMessage);

		/** Student added label */
		lblStudentAddedMessage = new JLabel("");
		lblStudentAddedMessage.setName("studentAddedLabel");
		GridBagConstraints gbc_lblStudentAddedMessage = new GridBagConstraints();
		gbc_lblStudentAddedMessage.insets = new Insets(0, 0, 5, 0);
		gbc_lblStudentAddedMessage.gridwidth = 2;
		gbc_lblStudentAddedMessage.gridx = 0;
		gbc_lblStudentAddedMessage.gridy = 12;
		studentPanel.add(lblStudentAddedMessage, gbc_lblStudentAddedMessage);

		/** Student error not removed label */
		lblStudentErrorNotRemovedMessage = new JLabel("");
		lblStudentErrorNotRemovedMessage.setName("studentErrorNotRemovedLabel");
		GridBagConstraints gbc_lblStudentErrorNotRemovedMessage = new GridBagConstraints();
		gbc_lblStudentErrorNotRemovedMessage.insets = new Insets(0, 0, 5, 0);
		gbc_lblStudentErrorNotRemovedMessage.gridwidth = 2;
		gbc_lblStudentErrorNotRemovedMessage.gridx = 0;
		gbc_lblStudentErrorNotRemovedMessage.gridy = 12;
		studentPanel.add(lblStudentErrorNotRemovedMessage, gbc_lblStudentErrorNotRemovedMessage);

		/** Student removed label */
		lblStudentRemovedMessage = new JLabel("");
		lblStudentRemovedMessage.setName("studentRemovedLabel");
		GridBagConstraints gbc_lblStudentRemovedMessage = new GridBagConstraints();
		gbc_lblStudentRemovedMessage.insets = new Insets(0, 0, 5, 0);
		gbc_lblStudentRemovedMessage.gridwidth = 2;
		gbc_lblStudentRemovedMessage.gridx = 0;
		gbc_lblStudentRemovedMessage.gridy = 12;
		studentPanel.add(lblStudentRemovedMessage, gbc_lblStudentRemovedMessage);

		/** Course error not added to student label */
		lblCourseErrorNotAddedToStudentMessage = new JLabel("");
		lblCourseErrorNotAddedToStudentMessage.setName("courseErrorNotAddedToStudentLabel");
		GridBagConstraints gbc_lblCourseErrorNotAddedToStudentMessage = new GridBagConstraints();
		gbc_lblCourseErrorNotAddedToStudentMessage.insets = new Insets(0, 0, 5, 0);
		gbc_lblCourseErrorNotAddedToStudentMessage.gridwidth = 2;
		gbc_lblCourseErrorNotAddedToStudentMessage.gridx = 0;
		gbc_lblCourseErrorNotAddedToStudentMessage.gridy = 12;
		studentPanel.add(lblCourseErrorNotAddedToStudentMessage, gbc_lblCourseErrorNotAddedToStudentMessage);

		/** Course added to student label */
		lblCourseAddedToStudentMessage = new JLabel("");
		lblCourseAddedToStudentMessage.setName("courseAddedToStudentLabel");
		GridBagConstraints gbc_lblCourseAddedToStudentMessage = new GridBagConstraints();
		gbc_lblCourseAddedToStudentMessage.insets = new Insets(0, 0, 5, 0);
		gbc_lblCourseAddedToStudentMessage.gridwidth = 2;
		gbc_lblCourseAddedToStudentMessage.gridx = 0;
		gbc_lblCourseAddedToStudentMessage.gridy = 12;
		studentPanel.add(lblCourseAddedToStudentMessage, gbc_lblCourseAddedToStudentMessage);

		/** Course error not removed from student */
		lblCourseErrorNotRemovedFromStudentMessage = new JLabel("");
		lblCourseErrorNotRemovedFromStudentMessage.setName("courseErrorNotRemovedFromStudentLabel");
		GridBagConstraints gbc_lblCourseErrorNotRemovedFromStudentMessage = new GridBagConstraints();
		gbc_lblCourseErrorNotRemovedFromStudentMessage.insets = new Insets(0, 0, 5, 0);
		gbc_lblCourseErrorNotRemovedFromStudentMessage.gridwidth = 2;
		gbc_lblCourseErrorNotRemovedFromStudentMessage.gridx = 0;
		gbc_lblCourseErrorNotRemovedFromStudentMessage.gridy = 12;
		studentPanel.add(lblCourseErrorNotRemovedFromStudentMessage, gbc_lblCourseErrorNotRemovedFromStudentMessage);

		/** Course removed from student */
		lblCourseRemovedFromStudentMessage = new JLabel("");
		lblCourseRemovedFromStudentMessage.setName("courseRemovedFromStudentLabel");
		GridBagConstraints gbc_lblCourseRemovedFromStudentMessage = new GridBagConstraints();
		gbc_lblCourseRemovedFromStudentMessage.insets = new Insets(0, 0, 5, 0);
		gbc_lblCourseRemovedFromStudentMessage.gridwidth = 2;
		gbc_lblCourseRemovedFromStudentMessage.gridx = 0;
		gbc_lblCourseRemovedFromStudentMessage.gridy = 12;
		studentPanel.add(lblCourseRemovedFromStudentMessage, gbc_lblCourseRemovedFromStudentMessage);
		
		/* ===================================================================================================
		 * =                                                                                                 =   
		 * =                                        Course Panel                                             =
		 * =                                                                                                 =
		 * ===================================================================================================*/

		coursePanel = new JPanel();
		GridBagConstraints gbc_coursePanel = new GridBagConstraints();
		gbc_coursePanel.gridwidth = 2;
		gbc_coursePanel.fill = GridBagConstraints.BOTH;
		gbc_coursePanel.gridx = 1;
		gbc_coursePanel.gridy = 0;
		contentPane.add(coursePanel, gbc_coursePanel);
		GridBagLayout gbl_coursePanel = new GridBagLayout();
		gbl_coursePanel.columnWidths = new int[] { 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30 };
		gbl_coursePanel.rowHeights = new int[] { 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30 };
		coursePanel.setLayout(gbl_coursePanel);

		lblCourse = new JLabel("Course");
		lblCourse.setName("coursePanel");
		GridBagConstraints gbc_lblCourse = new GridBagConstraints();
		gbc_lblCourse.gridwidth = 4;
		gbc_lblCourse.insets = new Insets(0, 0, 5, 0);
		gbc_lblCourse.gridx = 0;
		gbc_lblCourse.gridy = 0;
		gbc_lblCourse.gridwidth = 11;
		coursePanel.add(lblCourse, gbc_lblCourse);

		lblCourseId = new JLabel("ID");
		lblCourseId.setName("courseIDLabel");
		GridBagConstraints gbc_lblCourseId = new GridBagConstraints();
		gbc_lblCourseId.anchor = GridBagConstraints.EAST;
		gbc_lblCourseId.insets = new Insets(0, 0, 5, 5);
		gbc_lblCourseId.gridx = 1;
		gbc_lblCourseId.gridy = 2;
		gbc_lblCourseId.gridwidth = 3;
		coursePanel.add(lblCourseId, gbc_lblCourseId);

		fieldCourseId = new JTextField();
		fieldCourseId.addKeyListener(addCourseButtonEnabler);
		fieldCourseId.setName("courseIDTextField");
		GridBagConstraints gbc_fieldCourseId = new GridBagConstraints();
		gbc_fieldCourseId.gridx = 4;
		gbc_fieldCourseId.gridy = 2;
		gbc_fieldCourseId.gridwidth = 7;
		gbc_fieldCourseId.insets = new Insets(0, 0, 5, 0);
		gbc_fieldCourseId.fill = GridBagConstraints.HORIZONTAL;
		coursePanel.add(fieldCourseId, gbc_fieldCourseId);
		fieldCourseId.setColumns(7);

		lblCourseName = new JLabel("Name");
		lblCourseName.setName("courseNameLabel");
		GridBagConstraints gbc_lblCourseName = new GridBagConstraints();
		gbc_lblCourseName.anchor = GridBagConstraints.EAST;
		gbc_lblCourseName.insets = new Insets(0, 0, 5, 5);
		gbc_lblCourseName.gridx = 1;
		gbc_lblCourseName.gridy = 3;
		gbc_lblCourseName.gridwidth = 3;
		coursePanel.add(lblCourseName, gbc_lblCourseName);

		fieldCourseName = new JTextField();
		fieldCourseName.addKeyListener(addCourseButtonEnabler);
		fieldCourseName.setName("courseNameTextField");
		GridBagConstraints gbc_fieldCourseName = new GridBagConstraints();
		gbc_fieldCourseName.gridx = 4;
		gbc_fieldCourseName.gridy = 3;
		gbc_fieldCourseName.gridwidth = 7;
		gbc_fieldCourseName.insets = new Insets(0, 0, 5, 0);
		gbc_fieldCourseName.fill = GridBagConstraints.HORIZONTAL;
		coursePanel.add(fieldCourseName, gbc_fieldCourseName);
		fieldCourseName.setColumns(7);

		lblCfu = new JLabel("CFU");
		lblCfu.setName("courseCFULabel");
		GridBagConstraints gbc_lblCfu = new GridBagConstraints();
		gbc_lblCfu.anchor = GridBagConstraints.EAST;
		gbc_lblCfu.insets = new Insets(0, 0, 5, 5);
		gbc_lblCfu.gridx = 1;
		gbc_lblCfu.gridy = 4;
		gbc_lblCfu.gridwidth = 3;
		coursePanel.add(lblCfu, gbc_lblCfu);

		fieldCourseCFU = new JTextField();
		fieldCourseCFU.addKeyListener(addCourseButtonEnabler);
		fieldCourseCFU.setName("courseCFUTextField");
		GridBagConstraints gbc_fieldCourseCFU = new GridBagConstraints();
		gbc_fieldCourseCFU.gridx = 4;
		gbc_fieldCourseCFU.gridy = 4;
		gbc_fieldCourseCFU.gridwidth = 7;
		gbc_fieldCourseCFU.insets = new Insets(0, 0, 5, 0);
		gbc_fieldCourseCFU.fill = GridBagConstraints.HORIZONTAL;
		coursePanel.add(fieldCourseCFU, gbc_fieldCourseCFU);
		fieldCourseCFU.setColumns(7);

		btnAddNewCourse = new JButton("Add new course");
		btnAddNewCourse.setName("addNewCourseButton");
		btnAddNewCourse.setEnabled(false);
		btnAddNewCourse.addActionListener(e -> agendaController.addCourse(new Course(fieldCourseId.getText(),
				fieldCourseName.getText(), fieldCourseCFU.getText())));
		GridBagConstraints gbc_btnAddNewCourse = new GridBagConstraints();
		gbc_btnAddNewCourse.gridx = 1;
		gbc_btnAddNewCourse.gridy = 6;
		gbc_btnAddNewCourse.gridwidth = 4;
		gbc_btnAddNewCourse.insets = new Insets(0, 0, 5, 0);
		coursePanel.add(btnAddNewCourse, gbc_btnAddNewCourse);

		btnAddStudentToCourse = new JButton("Add to course");
		btnAddStudentToCourse.setName("addStudentToCourseButton");
		btnAddStudentToCourse.setEnabled(false);
		btnAddStudentToCourse.addActionListener(e -> agendaController.addStudentToCourse(studentsList.getSelectedValue(), coursesList.getSelectedValue()));
		GridBagConstraints gbc_btnAddStudentToCourse = new GridBagConstraints();
		gbc_btnAddStudentToCourse.gridx = 7;
		gbc_btnAddStudentToCourse.gridy = 6;
		gbc_btnAddStudentToCourse.gridwidth = 4;
		gbc_btnAddStudentToCourse.insets = new Insets(0, 0, 5, 0);
		coursePanel.add(btnAddStudentToCourse, gbc_btnAddStudentToCourse);

		scrollPaneCourses = new JScrollPane();
		GridBagConstraints gbc_scrollPaneCourses = new GridBagConstraints();
		gbc_scrollPaneCourses.gridx = 1;
		gbc_scrollPaneCourses.gridy = 7;
		gbc_scrollPaneCourses.gridwidth = 4;
		gbc_scrollPaneCourses.gridheight = 4;
		gbc_scrollPaneCourses.insets = new Insets(2, 0, 2, 0);
		gbc_scrollPaneCourses.fill = GridBagConstraints.BOTH;
		coursePanel.add(scrollPaneCourses, gbc_scrollPaneCourses);

		coursesListModel = new DefaultListModel<>();
		coursesList = new JList<>(coursesListModel);
		// FIXME
		coursesList.addListSelectionListener(e -> {
			btnRemoveCourse.setEnabled(coursesList.getSelectedIndex() != -1);
			if (studentsList.getSelectedIndex() != -1) {
				btnAddCourseToStudent.setEnabled(studentsList.getSelectedIndex() != -1 && coursesList.getSelectedIndex() != -1);
				btnAddStudentToCourse.setEnabled(studentsList.getSelectedIndex() != -1 && coursesList.getSelectedIndex() != -1);
			}
		});
		coursesList.setName("coursesList");
		coursesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneCourses.setViewportView(coursesList);

		scrollPaneCourseStudents = new JScrollPane();
		GridBagConstraints gbc_scrollPaneCourseStudents = new GridBagConstraints();
		gbc_scrollPaneCourseStudents.gridx = 7;
		gbc_scrollPaneCourseStudents.gridy = 7;
		gbc_scrollPaneCourseStudents.gridwidth = 4;
		gbc_scrollPaneCourseStudents.gridheight = 4;
		gbc_scrollPaneCourseStudents.insets = new Insets(2, 0, 2, 0);
		gbc_scrollPaneCourseStudents.fill = GridBagConstraints.BOTH;
		coursePanel.add(scrollPaneCourseStudents, gbc_scrollPaneCourseStudents);

		courseStudentsListModel = new DefaultListModel<>();
		courseStudentsList = new JList<>(courseStudentsListModel);
		// FIXME
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
		gbc_btnRemoveCourse.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemoveCourse.gridx = 1;
		gbc_btnRemoveCourse.gridy = 11;
		gbc_btnRemoveCourse.gridwidth = 4;
		coursePanel.add(btnRemoveCourse, gbc_btnRemoveCourse);

		btnRemoveStudentFromCourse = new JButton("Remove from course");
		btnRemoveStudentFromCourse.setName("removeStudentFromCourseButton");
		btnRemoveStudentFromCourse.setEnabled(false);
		btnRemoveStudentFromCourse.addActionListener(e -> agendaController.removeStudentFromCourse(courseStudentsList.getSelectedValue(), coursesList.getSelectedValue()));
		GridBagConstraints gbc_btnRemoveStudentFromCourse = new GridBagConstraints();
		gbc_btnRemoveStudentFromCourse.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemoveStudentFromCourse.gridx = 7;
		gbc_btnRemoveStudentFromCourse.gridy = 11;
		gbc_btnRemoveStudentFromCourse.gridwidth = 4;
		coursePanel.add(btnRemoveStudentFromCourse, gbc_btnRemoveStudentFromCourse);

		/** Course error not added label */
		lblCourseErrorNotAddedMessage = new JLabel("");
		lblCourseErrorNotAddedMessage.setName("courseErrorNotAddedLabel");
		GridBagConstraints gbc_lblCourseErrorNotAddedMessage = new GridBagConstraints();
		gbc_lblCourseErrorNotAddedMessage.insets = new Insets(0, 0, 5, 0);
		gbc_lblCourseErrorNotAddedMessage.gridwidth = 2;
		gbc_lblCourseErrorNotAddedMessage.gridx = 0;
		gbc_lblCourseErrorNotAddedMessage.gridy = 12;
		coursePanel.add(lblCourseErrorNotAddedMessage, gbc_lblCourseErrorNotAddedMessage);

		/** Course added label */
		lblCourseAddedMessage = new JLabel("");
		lblCourseAddedMessage.setName("courseAddedLabel");
		GridBagConstraints gbc_lblCourseAddedMessage = new GridBagConstraints();
		gbc_lblCourseAddedMessage.insets = new Insets(0, 0, 5, 0);
		gbc_lblCourseAddedMessage.gridwidth = 2;
		gbc_lblCourseAddedMessage.gridx = 0;
		gbc_lblCourseAddedMessage.gridy = 12;
		coursePanel.add(lblCourseAddedMessage, gbc_lblCourseAddedMessage);

		/** Course error not removed label */
		lblCourseErrorNotRemovedMessage = new JLabel("");
		lblCourseErrorNotRemovedMessage.setName("courseErrorNotRemovedLabel");
		GridBagConstraints gbc_lblCourseErrorNotRemovedMessage = new GridBagConstraints();
		gbc_lblCourseErrorNotRemovedMessage.insets = new Insets(0, 0, 5, 0);
		gbc_lblCourseErrorNotRemovedMessage.gridwidth = 2;
		gbc_lblCourseErrorNotRemovedMessage.gridx = 0;
		gbc_lblCourseErrorNotRemovedMessage.gridy = 12;
		coursePanel.add(lblCourseErrorNotRemovedMessage, gbc_lblCourseErrorNotRemovedMessage);

		/** Course removed label */
		lblCourseRemovedMessage = new JLabel("");
		lblCourseRemovedMessage.setName("courseRemovedLabel");
		GridBagConstraints gbc_lblCourseRemovedMessage = new GridBagConstraints();
		gbc_lblCourseRemovedMessage.insets = new Insets(0, 0, 5, 0);
		gbc_lblCourseRemovedMessage.gridwidth = 2;
		gbc_lblCourseRemovedMessage.gridx = 0;
		gbc_lblCourseRemovedMessage.gridy = 12;
		coursePanel.add(lblCourseRemovedMessage, gbc_lblCourseRemovedMessage);
		
		/** Student error not added to course label */
		lblStudentErrorNotAddedToCourseMessage = new JLabel("");
		lblStudentErrorNotAddedToCourseMessage.setName("studentErrorNotAddedToCourseLabel");
		GridBagConstraints gbc_lblStudentErrorNotAddedToCourseMessage = new GridBagConstraints();
		gbc_lblStudentErrorNotAddedToCourseMessage.insets = new Insets(0, 0, 5, 0);
		gbc_lblStudentErrorNotAddedToCourseMessage.gridwidth = 2;
		gbc_lblStudentErrorNotAddedToCourseMessage.gridx = 0;
		gbc_lblStudentErrorNotAddedToCourseMessage.gridy = 12;
		coursePanel.add(lblStudentErrorNotAddedToCourseMessage, gbc_lblStudentErrorNotAddedToCourseMessage);

		/** Student added to course label */
		lblStudentAddedToCourseMessage = new JLabel("");
		lblStudentAddedToCourseMessage.setName("studentAddedToCourseLabel");
		GridBagConstraints gbc_lblStudentAddedToCourseMessage = new GridBagConstraints();
		gbc_lblStudentAddedToCourseMessage.insets = new Insets(0, 0, 5, 0);
		gbc_lblStudentAddedToCourseMessage.gridwidth = 2;
		gbc_lblStudentAddedToCourseMessage.gridx = 0;
		gbc_lblStudentAddedToCourseMessage.gridy = 12;
		coursePanel.add(lblStudentAddedToCourseMessage, gbc_lblStudentAddedToCourseMessage);

		/** Student error not removed from course label */
		lblStudentErrorNotRemovedFromCourseMessage = new JLabel("");
		lblStudentErrorNotRemovedFromCourseMessage.setName("studentErrorNotRemovedFromCourseLabel");
		GridBagConstraints gbc_lblStudentErrorNotRemovedFromCourseMessage = new GridBagConstraints();
		gbc_lblStudentErrorNotRemovedFromCourseMessage.insets = new Insets(0, 0, 5, 0);
		gbc_lblStudentErrorNotRemovedFromCourseMessage.gridwidth = 2;
		gbc_lblStudentErrorNotRemovedFromCourseMessage.gridx = 0;
		gbc_lblStudentErrorNotRemovedFromCourseMessage.gridy = 12;
		coursePanel.add(lblStudentErrorNotRemovedFromCourseMessage, gbc_lblStudentErrorNotRemovedFromCourseMessage);

		/** Student removed from course label */
		lblStudentRemovedFromCourseMessage = new JLabel("");
		lblStudentRemovedFromCourseMessage.setName("studentRemovedFromCourseLabel");
		GridBagConstraints gbc_lblStudentRemovedFromCourseMessage = new GridBagConstraints();
		gbc_lblStudentRemovedFromCourseMessage.insets = new Insets(0, 0, 5, 0);
		gbc_lblStudentRemovedFromCourseMessage.gridwidth = 2;
		gbc_lblStudentRemovedFromCourseMessage.gridx = 0;
		gbc_lblStudentRemovedFromCourseMessage.gridy = 12;
		coursePanel.add(lblStudentRemovedFromCourseMessage, gbc_lblStudentRemovedFromCourseMessage);
	}

	@Override
	public void showAllStudents(List<Student> allStudents) {
		allStudents.stream().forEach(studentsListModel::addElement); 
	}

	@Override
	public void notifyStudentAdded(Student student) {
		studentsListModel.addElement(student);
		lblStudentAddedMessage.setText(student.toString() + " successfully added!");
	}

	@Override
	public void notifyStudentNotAdded(Student student) {
		lblStudentErrorNotAddedMessage.setText("ERROR! " + student.toString() + " NOT added!");
	}

	@Override
	public void notifyStudentRemoved(Student student) {
		studentsListModel.removeElement(student);
		lblStudentRemovedMessage.setText(student.toString() + " successfully removed!");
	}

	@Override
	public void notifyStudentNotRemoved(Student student) {
		lblStudentErrorNotRemovedMessage.setText("ERROR! " + student.toString() + " NOT removed!");
	}

	@Override
	public void notifyCourseAddedToStudent(Student student, Course course) {
		studentCoursesListModel.addElement(course);
		lblCourseAddedToStudentMessage.setText(course.toString() + " added to " + student.toString());
	}

	@Override
	public void notifyCourseNotAddedToStudent(Student student, Course course) {
		lblCourseErrorNotAddedToStudentMessage.setText("ERROR! " + course.toString() + " NOT added to " + student.toString());
	}

	@Override
	public void notifyCourseRemovedFromStudent(Student student, Course course) {
		studentCoursesListModel.removeElement(course);
		lblCourseRemovedFromStudentMessage.setText(course.toString() + " removed from " + student.toString());
	}

	@Override
	public void notifyCourseNotRemovedFromStudent(Student student, Course course) {
		lblCourseErrorNotRemovedFromStudentMessage.setText("ERROR! " + course.toString() + " NOT removed from " + student.toString());
	}

	@Override
	public void notifyCourseAdded(Course course) {
		coursesListModel.addElement(course);
		lblCourseAddedMessage.setText(course.toString() + " successfully added!");
	}

	@Override
	public void notifyCourseNotAdded(Course course) {
		lblCourseErrorNotAddedMessage.setText("ERROR! " + course.toString() + " NOT added!");
	}

	@Override
	public void notifyCourseRemoved(Course course) {
		coursesListModel.removeElement(course);
		lblCourseRemovedMessage.setText(course.toString() + " successfully removed!");
	}

	@Override
	public void notifyCourseNotRemoved(Course course) {
		lblCourseErrorNotRemovedMessage.setText("ERROR! " + course.toString() + " NOT removed!");
	}

	@Override
	public void notifyStudentRemovedFromCourse(Student student, Course course) {
		courseStudentsListModel.removeElement(student);
		lblStudentRemovedFromCourseMessage.setText(student.toString() + " removed from " + course.toString());
	}

	@Override
	public void notifyStudentNotRemovedFromCourse(Student student, Course course) {
		lblStudentErrorNotRemovedFromCourseMessage.setText("ERROR! " + student.toString() + " NOT removed from " + course.toString());
	}

	@Override
	public void notifyStudentNotAddedToCourse(Student student, Course course) {
		lblStudentErrorNotAddedToCourseMessage.setText("ERROR! " + student.toString() + " NOT added to " + course.toString());
	}

	@Override
	public void notifyStudentAddedToCourse(Student student, Course course) {
		courseStudentsListModel.addElement(student);
		lblStudentAddedToCourseMessage.setText(student.toString() + " added to " + course.toString());
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
