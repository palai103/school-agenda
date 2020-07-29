package view.swing;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import controller.AgendaController;
import model.Course;
import model.Student;
import view.AgendaView;
import javax.swing.JScrollPane;

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
    private JButton btnRemoveStudent;
    private JLabel lblStudentInfoErrorMessage;
    private JPanel coursePanel;
    private JLabel lblCourse;
    private JLabel lblCourseId;
    private JLabel lblCourseName;
    private JLabel lblCfu;
    private JButton btnAddNewCourse;
    private JButton btnRemoveCourse;
    private JLabel lblCourseInfoErrorMessage;
    private JScrollPane scrollPane;
    private JScrollPane scrollPane_1;



    public void setAgendaController(AgendaController agendaController) {
        this.agendaController = agendaController;
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
    @SuppressWarnings("rawtypes")
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

        setTitle("School Agenda");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 550);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[] { 450, 450, 0 };
        gbl_contentPane.rowHeights = new int[] { 0, 0 };
        gbl_contentPane.columnWeights = new double[] { 1.0, 4.9E-324 };
        gbl_contentPane.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        contentPane.setLayout(gbl_contentPane);

        studentPanel = new JPanel();
        GridBagConstraints gbc_studentPanel = new GridBagConstraints();
        gbc_studentPanel.insets = new Insets(0, 0, 0, 5);
        gbc_studentPanel.fill = GridBagConstraints.BOTH;
        gbc_studentPanel.gridx = 0;
        gbc_studentPanel.gridy = 0;
        contentPane.add(studentPanel, gbc_studentPanel);
        GridBagLayout gbl_studentPanel = new GridBagLayout();
        gbl_studentPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_studentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_studentPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gbl_studentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, Double.MIN_VALUE };
        studentPanel.setLayout(gbl_studentPanel);

        lblStudent = new JLabel("Student");
        lblStudent.setName("studentPanel");
        GridBagConstraints gbc_lblStudent = new GridBagConstraints();
        gbc_lblStudent.insets = new Insets(0, 0, 5, 0);
        gbc_lblStudent.gridx = 1;
        gbc_lblStudent.gridy = 0;
        studentPanel.add(lblStudent, gbc_lblStudent);

        lblStudentId = new JLabel("ID");
        lblStudentId.setName("studentIDLabel");
        GridBagConstraints gbc_lblStudentId = new GridBagConstraints();
        gbc_lblStudentId.anchor = GridBagConstraints.EAST;
        gbc_lblStudentId.insets = new Insets(0, 0, 5, 5);
        gbc_lblStudentId.gridx = 0;
        gbc_lblStudentId.gridy = 2;
        studentPanel.add(lblStudentId, gbc_lblStudentId);

        fieldStudentId = new JTextField();
        fieldStudentId.addKeyListener(addStudentButtonEnabler);
        fieldStudentId.setName("studentIDTextField");
        GridBagConstraints gbc_fieldStudentId = new GridBagConstraints();
        gbc_fieldStudentId.insets = new Insets(0, 0, 5, 0);
        gbc_fieldStudentId.fill = GridBagConstraints.HORIZONTAL;
        gbc_fieldStudentId.gridx = 1;
        gbc_fieldStudentId.gridy = 2;
        studentPanel.add(fieldStudentId, gbc_fieldStudentId);
        fieldStudentId.setColumns(10);

        lblStudentName = new JLabel("Name");
        lblStudentName.setName("studentNameLabel");
        GridBagConstraints gbc_lblStudentName = new GridBagConstraints();
        gbc_lblStudentName.anchor = GridBagConstraints.EAST;
        gbc_lblStudentName.insets = new Insets(0, 0, 5, 5);
        gbc_lblStudentName.gridx = 0;
        gbc_lblStudentName.gridy = 3;
        studentPanel.add(lblStudentName, gbc_lblStudentName);

        fieldStudentName = new JTextField();
        fieldStudentName.addKeyListener(addStudentButtonEnabler);
        fieldStudentName.setName("studentNameTextField");
        GridBagConstraints gbc_fieldStudentName = new GridBagConstraints();
        gbc_fieldStudentName.insets = new Insets(0, 0, 5, 0);
        gbc_fieldStudentName.fill = GridBagConstraints.HORIZONTAL;
        gbc_fieldStudentName.gridx = 1;
        gbc_fieldStudentName.gridy = 3;
        studentPanel.add(fieldStudentName, gbc_fieldStudentName);
        fieldStudentName.setColumns(10);

        btnAddNewStudent = new JButton("Add new student");
        btnAddNewStudent.setName("addNewStudentButton");
        btnAddNewStudent.setEnabled(false);
        GridBagConstraints gbc_btnAddNewStudent = new GridBagConstraints();
        gbc_btnAddNewStudent.insets = new Insets(0, 0, 5, 0);
        gbc_btnAddNewStudent.gridx = 1;
        gbc_btnAddNewStudent.gridy = 5;
        studentPanel.add(btnAddNewStudent, gbc_btnAddNewStudent);
        
        scrollPane_1 = new JScrollPane();
        GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
        gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_1.gridx = 1;
        gbc_scrollPane_1.gridy = 6;
        studentPanel.add(scrollPane_1, gbc_scrollPane_1);

        btnRemoveStudent = new JButton("Remove student");
        btnRemoveStudent.setName("removeStudentButton");
        btnRemoveStudent.setEnabled(false);
        GridBagConstraints gbc_btnRemoveStudent = new GridBagConstraints();
        gbc_btnRemoveStudent.gridx = 1;
        gbc_btnRemoveStudent.gridy = 8;
        studentPanel.add(btnRemoveStudent, gbc_btnRemoveStudent);

        lblStudentInfoErrorMessage = new JLabel("");
        lblStudentInfoErrorMessage.setName("studentInfoErrorMessageLabel");
        GridBagConstraints gbc_lblStudentInfoErrorMessage = new GridBagConstraints();
        gbc_lblStudentInfoErrorMessage.insets = new Insets(0, 0, 5, 0);
        gbc_lblStudentInfoErrorMessage.gridwidth = 2;
        gbc_lblStudentInfoErrorMessage.gridx = 0;
        gbc_lblStudentInfoErrorMessage.gridy = 7;
        studentPanel.add(lblStudentInfoErrorMessage, gbc_lblStudentInfoErrorMessage);

        coursePanel = new JPanel();
        GridBagConstraints gbc_coursePanel = new GridBagConstraints();
        gbc_coursePanel.fill = GridBagConstraints.BOTH;
        gbc_coursePanel.gridx = 1;
        gbc_coursePanel.gridy = 0;
        contentPane.add(coursePanel, gbc_coursePanel);
        GridBagLayout gbl_coursePanel = new GridBagLayout();
        gbl_coursePanel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gbl_coursePanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_coursePanel.columnWeights = new double[] { 0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
        gbl_coursePanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
        coursePanel.setLayout(gbl_coursePanel);

        lblCourse = new JLabel("Course");
        lblCourse.setName("coursePanel");
        GridBagConstraints gbc_lblCourse = new GridBagConstraints();
        gbc_lblCourse.gridwidth = 4;
        gbc_lblCourse.insets = new Insets(0, 0, 5, 0);
        gbc_lblCourse.gridx = 0;
        gbc_lblCourse.gridy = 0;
        coursePanel.add(lblCourse, gbc_lblCourse);

        lblCourseId = new JLabel("ID");
        lblCourseId.setName("courseIDLabel");
        GridBagConstraints gbc_lblCourseId = new GridBagConstraints();
        gbc_lblCourseId.anchor = GridBagConstraints.EAST;
        gbc_lblCourseId.insets = new Insets(0, 0, 5, 5);
        gbc_lblCourseId.gridx = 0;
        gbc_lblCourseId.gridy = 2;
        coursePanel.add(lblCourseId, gbc_lblCourseId);

        fieldCourseId = new JTextField();
        fieldCourseId.setName("courseIDTextField");
        GridBagConstraints gbc_fieldCourseId = new GridBagConstraints();
        gbc_fieldCourseId.gridwidth = 3;
        gbc_fieldCourseId.insets = new Insets(0, 0, 5, 0);
        gbc_fieldCourseId.fill = GridBagConstraints.HORIZONTAL;
        gbc_fieldCourseId.gridx = 1;
        gbc_fieldCourseId.gridy = 2;
        coursePanel.add(fieldCourseId, gbc_fieldCourseId);
        fieldCourseId.setColumns(10);

        lblCourseName = new JLabel("Name");
        lblCourseName.setName("courseNameLabel");
        GridBagConstraints gbc_lblCourseName = new GridBagConstraints();
        gbc_lblCourseName.anchor = GridBagConstraints.EAST;
        gbc_lblCourseName.insets = new Insets(0, 0, 5, 5);
        gbc_lblCourseName.gridx = 0;
        gbc_lblCourseName.gridy = 3;
        coursePanel.add(lblCourseName, gbc_lblCourseName);

        fieldCourseName = new JTextField();
        fieldCourseName.setName("courseNameTextField");
        GridBagConstraints gbc_fieldCourseName = new GridBagConstraints();
        gbc_fieldCourseName.gridwidth = 3;
        gbc_fieldCourseName.insets = new Insets(0, 0, 5, 0);
        gbc_fieldCourseName.fill = GridBagConstraints.HORIZONTAL;
        gbc_fieldCourseName.gridx = 1;
        gbc_fieldCourseName.gridy = 3;
        coursePanel.add(fieldCourseName, gbc_fieldCourseName);
        fieldCourseName.setColumns(10);

        lblCfu = new JLabel("CFU");
        lblCfu.setName("courseCFULabel");
        GridBagConstraints gbc_lblCfu = new GridBagConstraints();
        gbc_lblCfu.anchor = GridBagConstraints.EAST;
        gbc_lblCfu.insets = new Insets(0, 0, 5, 5);
        gbc_lblCfu.gridx = 0;
        gbc_lblCfu.gridy = 4;
        coursePanel.add(lblCfu, gbc_lblCfu);

        fieldCourseCFU = new JTextField();
        fieldCourseCFU.setName("courseCFUTextField");
        GridBagConstraints gbc_fieldCourseCFU = new GridBagConstraints();
        gbc_fieldCourseCFU.gridwidth = 3;
        gbc_fieldCourseCFU.insets = new Insets(0, 0, 5, 0);
        gbc_fieldCourseCFU.fill = GridBagConstraints.HORIZONTAL;
        gbc_fieldCourseCFU.gridx = 1;
        gbc_fieldCourseCFU.gridy = 4;
        coursePanel.add(fieldCourseCFU, gbc_fieldCourseCFU);
        fieldCourseCFU.setColumns(10);

        btnAddNewCourse = new JButton("Add new course");
        btnAddNewCourse.setName("addNewCourseButton");
        btnAddNewCourse.setEnabled(false);
        GridBagConstraints gbc_btnAddNewCourse = new GridBagConstraints();
        gbc_btnAddNewCourse.insets = new Insets(0, 0, 5, 5);
        gbc_btnAddNewCourse.gridx = 2;
        gbc_btnAddNewCourse.gridy = 5;
        coursePanel.add(btnAddNewCourse, gbc_btnAddNewCourse);

        scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridwidth = 2;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 1;
        gbc_scrollPane.gridy = 6;
        coursePanel.add(scrollPane, gbc_scrollPane);

        btnRemoveCourse = new JButton("Remove Course");
        btnRemoveCourse.setName("removeCourseButton");
        btnRemoveCourse.setEnabled(false);
        GridBagConstraints gbc_btnRemoveCourse = new GridBagConstraints();
        gbc_btnRemoveCourse.insets = new Insets(0, 0, 0, 5);
        gbc_btnRemoveCourse.gridx = 2;
        gbc_btnRemoveCourse.gridy = 8;
        coursePanel.add(btnRemoveCourse, gbc_btnRemoveCourse);

        lblCourseInfoErrorMessage = new JLabel("");
        lblCourseInfoErrorMessage.setName("courseInfoErrorMessageLabel");
        GridBagConstraints gbc_lblCourseInfoErrorMessage = new GridBagConstraints();
        gbc_lblCourseInfoErrorMessage.insets = new Insets(0, 0, 5, 0);
        gbc_lblCourseInfoErrorMessage.gridwidth = 2;
        gbc_lblCourseInfoErrorMessage.gridx = 0;
        gbc_lblCourseInfoErrorMessage.gridy = 7;
        studentPanel.add(lblCourseInfoErrorMessage, gbc_lblCourseInfoErrorMessage);

    }

    @Override
    public void showAllStudents(List<Student> allStudents) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyStudentAdded(Student student) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyStudentNotAdded(Student student) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyStudentRemoved(Student student) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyStudentNotRemoved(Student student) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyCourseAddedToStudent(Student student, Course course) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyCourseNotAddedToStudent(Student student, Course course) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyCourseRemovedFromStudent(Student student, Course course) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyCourseNotRemovedFromStudent(Student student, Course course) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyCourseAdded(Course course) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyCourseNotAdded(Course course) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyCourseRemoved(Course course) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyCourseNotRemoved(Course course) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyStudentRemovedFromCourse(Student student, Course course) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyStudentNotRemovedFromCourse(Student student, Course course) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyStudentNotAddedToCourse(Student student, Course course) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyStudentAddedToCourse(Student student, Course course) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showAllCourses(List<Course> allCourses) {
        // TODO Auto-generated method stub

    }
}
