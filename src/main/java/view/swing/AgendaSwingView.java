package view.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

public class AgendaSwingView extends JFrame {

	private JPanel contentPane;
	private JTextField idField;
	private JTextField nameField;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;

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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 550);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{450, 450, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 4.9E-324};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JPanel studentPanel = new JPanel();
		GridBagConstraints gbc_studentPanel = new GridBagConstraints();
		gbc_studentPanel.insets = new Insets(0, 0, 0, 5);
		gbc_studentPanel.fill = GridBagConstraints.BOTH;
		gbc_studentPanel.gridx = 0;
		gbc_studentPanel.gridy = 0;
		contentPane.add(studentPanel, gbc_studentPanel);
		GridBagLayout gbl_studentPanel = new GridBagLayout();
		gbl_studentPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_studentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_studentPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_studentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		studentPanel.setLayout(gbl_studentPanel);
		
		JLabel lblStudent = new JLabel("Student");
		GridBagConstraints gbc_lblStudent = new GridBagConstraints();
		gbc_lblStudent.insets = new Insets(0, 0, 5, 0);
		gbc_lblStudent.gridx = 1;
		gbc_lblStudent.gridy = 0;
		studentPanel.add(lblStudent, gbc_lblStudent);
		
		JLabel idLabel = new JLabel("ID");
		GridBagConstraints gbc_idLabel = new GridBagConstraints();
		gbc_idLabel.anchor = GridBagConstraints.EAST;
		gbc_idLabel.insets = new Insets(0, 0, 5, 5);
		gbc_idLabel.gridx = 0;
		gbc_idLabel.gridy = 2;
		studentPanel.add(idLabel, gbc_idLabel);
		
		idField = new JTextField();
		GridBagConstraints gbc_idField = new GridBagConstraints();
		gbc_idField.insets = new Insets(0, 0, 5, 0);
		gbc_idField.fill = GridBagConstraints.HORIZONTAL;
		gbc_idField.gridx = 1;
		gbc_idField.gridy = 2;
		studentPanel.add(idField, gbc_idField);
		idField.setColumns(10);
		
		JLabel nameLabel = new JLabel("Name");
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.anchor = GridBagConstraints.EAST;
		gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_nameLabel.gridx = 0;
		gbc_nameLabel.gridy = 3;
		studentPanel.add(nameLabel, gbc_nameLabel);
		
		nameField = new JTextField();
		GridBagConstraints gbc_nameField = new GridBagConstraints();
		gbc_nameField.insets = new Insets(0, 0, 5, 0);
		gbc_nameField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameField.gridx = 1;
		gbc_nameField.gridy = 3;
		studentPanel.add(nameField, gbc_nameField);
		nameField.setColumns(10);
		
		JButton btnNewButton = new JButton("Add new student");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 5;
		studentPanel.add(btnNewButton, gbc_btnNewButton);
		
		JPanel coursePanel = new JPanel();
		GridBagConstraints gbc_coursePanel = new GridBagConstraints();
		gbc_coursePanel.fill = GridBagConstraints.BOTH;
		gbc_coursePanel.gridx = 1;
		gbc_coursePanel.gridy = 0;
		contentPane.add(coursePanel, gbc_coursePanel);
		GridBagLayout gbl_coursePanel = new GridBagLayout();
		gbl_coursePanel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_coursePanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_coursePanel.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_coursePanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		coursePanel.setLayout(gbl_coursePanel);
		
		JLabel lblCourse = new JLabel("Course");
		GridBagConstraints gbc_lblCourse = new GridBagConstraints();
		gbc_lblCourse.gridwidth = 4;
		gbc_lblCourse.insets = new Insets(0, 0, 5, 0);
		gbc_lblCourse.gridx = 0;
		gbc_lblCourse.gridy = 0;
		coursePanel.add(lblCourse, gbc_lblCourse);
		
		JLabel lblId_1 = new JLabel("ID");
		GridBagConstraints gbc_lblId_1 = new GridBagConstraints();
		gbc_lblId_1.anchor = GridBagConstraints.EAST;
		gbc_lblId_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblId_1.gridx = 0;
		gbc_lblId_1.gridy = 2;
		coursePanel.add(lblId_1, gbc_lblId_1);
		
		textField_2 = new JTextField();
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.gridwidth = 3;
		gbc_textField_2.insets = new Insets(0, 0, 5, 0);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 1;
		gbc_textField_2.gridy = 2;
		coursePanel.add(textField_2, gbc_textField_2);
		textField_2.setColumns(10);
		
		JLabel lblName_1 = new JLabel("Name");
		GridBagConstraints gbc_lblName_1 = new GridBagConstraints();
		gbc_lblName_1.anchor = GridBagConstraints.EAST;
		gbc_lblName_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblName_1.gridx = 0;
		gbc_lblName_1.gridy = 3;
		coursePanel.add(lblName_1, gbc_lblName_1);
		
		textField_3 = new JTextField();
		GridBagConstraints gbc_textField_3 = new GridBagConstraints();
		gbc_textField_3.gridwidth = 3;
		gbc_textField_3.insets = new Insets(0, 0, 5, 0);
		gbc_textField_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_3.gridx = 1;
		gbc_textField_3.gridy = 3;
		coursePanel.add(textField_3, gbc_textField_3);
		textField_3.setColumns(10);
		
		JLabel lblCfu = new JLabel("CFU");
		GridBagConstraints gbc_lblCfu = new GridBagConstraints();
		gbc_lblCfu.anchor = GridBagConstraints.EAST;
		gbc_lblCfu.insets = new Insets(0, 0, 5, 5);
		gbc_lblCfu.gridx = 0;
		gbc_lblCfu.gridy = 4;
		coursePanel.add(lblCfu, gbc_lblCfu);
		
		textField_4 = new JTextField();
		GridBagConstraints gbc_textField_4 = new GridBagConstraints();
		gbc_textField_4.gridwidth = 3;
		gbc_textField_4.insets = new Insets(0, 0, 5, 0);
		gbc_textField_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_4.gridx = 1;
		gbc_textField_4.gridy = 4;
		coursePanel.add(textField_4, gbc_textField_4);
		textField_4.setColumns(10);
		
		JButton btnAddNewCourse = new JButton("Add new course");
		GridBagConstraints gbc_btnAddNewCourse = new GridBagConstraints();
		gbc_btnAddNewCourse.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddNewCourse.gridx = 2;
		gbc_btnAddNewCourse.gridy = 5;
		coursePanel.add(btnAddNewCourse, gbc_btnAddNewCourse);
	}

}
