package apt.project.frontend.view.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import apt.project.backend.domain.Student;
import apt.project.frontend.controller.StudentController;
import apt.project.frontend.view.MainFrame;
import apt.project.frontend.view.View;

public class StudentPanel extends JPanel implements View<Student> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JButton btnAdd;
    private JButton btnModify;
    private JButton btnDelete;
    private DefaultListModel<Student> listModel;
    private JList<Student> list;
    private transient StudentController studentController;
    private transient MainFrame parentMainFrame;

    public StudentPanel(MainFrame parentMainFrame,
            DialogManager dialogManager) {

        this.setParentMainFrame(parentMainFrame);

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 0.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);

        btnAdd = new JButton("Add");
        btnAdd.addActionListener(e -> {
            String name = dialogManager.manageDialog("Name");
            if (name != null) {
                studentController.newEntity(new Student(name));
            }
        });
        GridBagConstraints gbc_btnAdd = new GridBagConstraints();
        gbc_btnAdd.insets = new Insets(0, 0, 0, 5);
        gbc_btnAdd.gridx = 0;
        gbc_btnAdd.gridy = 1;
        add(btnAdd, gbc_btnAdd);

        btnModify = new JButton("Modify");
        btnModify.setEnabled(false);
        GridBagConstraints gbc_btnModify = new GridBagConstraints();
        gbc_btnModify.insets = new Insets(0, 0, 0, 5);
        gbc_btnModify.gridx = 1;
        gbc_btnModify.gridy = 1;
        add(btnModify, gbc_btnModify);
        btnModify.addActionListener(e -> {
            Student selectedStudent = list.getSelectedValue();
            String name = dialogManager.manageDialog("Name",
                    selectedStudent.getName());
            if (name != null) {
                studentController.updateEntity(selectedStudent,
                        new Student(name));
            }
        });

        btnDelete = new JButton("Delete");
        btnDelete.setEnabled(false);
        GridBagConstraints gbc_btnDelete = new GridBagConstraints();
        gbc_btnDelete.gridx = 2;
        gbc_btnDelete.gridy = 1;
        add(btnDelete, gbc_btnDelete);
        btnDelete.addActionListener(
                e -> studentController.deleteEntity(list.getSelectedValue()));

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridwidth = 3;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        add(scrollPane, gbc_scrollPane);

        listModel = new DefaultListModel<>();
        list = new JList<>(getListModel());
        list.setName("studentList");
        scrollPane.setViewportView(list);
        list.addListSelectionListener(e -> {
            boolean enable = list.getSelectedIndex() != -1;
            btnDelete.setEnabled(enable);
            btnModify.setEnabled(enable);
        });

        JLabel lblListOfCourses = new JLabel("List of Students");
        lblListOfCourses.setHorizontalAlignment(SwingConstants.CENTER);
        scrollPane.setColumnHeaderView(lblListOfCourses);

    }

    @Override
    public void showAll(List<Student> students) {
        students.stream().forEach(listModel::addElement);
    }

    @Override
    public void entityAdded(Student entity) {
        listModel.addElement(entity);
        parentMainFrame.resetErrorLabel();
    }

    @Override
    public void showError(String label, Student entity) {
        parentMainFrame.displayErrorLabel(label + ": " + entity);
    }

    @Override
    public void entityDeleted(Student entity) {
        listModel.removeElement(entity);
        parentMainFrame.resetErrorLabel();
    }

    @Override
    public void entityUpdated(Student existingEntity, Student modifiedEntity) {
        listModel.set(listModel.indexOf(existingEntity), modifiedEntity);
        parentMainFrame.resetErrorLabel();
    }

    public StudentController getStudentController() {
        return studentController;
    }

    public void setStudentController(StudentController studentController) {
        this.studentController = studentController;
    }

    DefaultListModel<Student> getListModel() {
        return listModel;
    }

    public MainFrame getParentMainFrame() {
        return parentMainFrame;
    }

    public void setParentMainFrame(MainFrame parentMainFrame) {
        this.parentMainFrame = parentMainFrame;
    }

}
