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

import apt.project.backend.domain.Course;
import apt.project.frontend.controller.CourseController;
import apt.project.frontend.view.MainFrame;
import apt.project.frontend.view.View;

public class CoursePanel extends JPanel implements View<Course> {

    private static final long serialVersionUID = 1L;

    private transient CourseController courseController;

    private JList<Course> list;
    private DefaultListModel<Course> listModel;

    private JButton btnAdd;
    private JButton btnModify;
    private JButton btnDelete;

    private transient MainFrame parentMainFrame;

    public CoursePanel(MainFrame parentMainFrame, DialogManager dialogManager) {

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
            String title = dialogManager.manageDialog("Title");
            if (title != null) {
                courseController.newEntity(new Course(title));
            }
        });
        GridBagConstraints gbc_btnAdd = new GridBagConstraints();
        gbc_btnAdd.insets = new Insets(0, 0, 0, 5);
        gbc_btnAdd.gridx = 0;
        gbc_btnAdd.gridy = 1;
        add(btnAdd, gbc_btnAdd);

        btnModify = new JButton("Modify");
        btnModify.setEnabled(false);
        btnModify.addActionListener(e -> {
            Course selectedCourse = list.getSelectedValue();
            String title = dialogManager.manageDialog("Title",
                    selectedCourse.getTitle());
            if (title != null) {
                courseController.updateEntity(selectedCourse,
                        new Course(title));
            }
        });
        GridBagConstraints gbc_btnModify = new GridBagConstraints();
        gbc_btnModify.insets = new Insets(0, 0, 0, 5);
        gbc_btnModify.gridx = 1;
        gbc_btnModify.gridy = 1;
        add(btnModify, gbc_btnModify);

        btnDelete = new JButton("Delete");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(
                e -> courseController.deleteEntity(list.getSelectedValue()));
        GridBagConstraints gbc_btnDelete = new GridBagConstraints();
        gbc_btnDelete.gridx = 2;
        gbc_btnDelete.gridy = 1;
        add(btnDelete, gbc_btnDelete);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridwidth = 3;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        add(scrollPane, gbc_scrollPane);

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setName("courseList");
        scrollPane.setViewportView(list);
        list.addListSelectionListener(e -> {
            boolean enable = list.getSelectedIndex() != -1;
            btnDelete.setEnabled(enable);
            btnModify.setEnabled(enable);
        });

        JLabel lblListOfCourses = new JLabel("List of Courses");
        lblListOfCourses.setHorizontalAlignment(SwingConstants.CENTER);
        scrollPane.setColumnHeaderView(lblListOfCourses);

    }

    @Override
    public void showAll(List<Course> courses) {
        courses.stream().forEach(listModel::addElement);
    }

    @Override
    public void entityAdded(Course entity) {
        listModel.addElement(entity);
        parentMainFrame.resetErrorLabel();
    }

    @Override
    public void entityDeleted(Course entity) {
        listModel.removeElement(entity);
        parentMainFrame.resetErrorLabel();
    }

    @Override
    public void entityUpdated(Course existingEntity, Course modifiedEntity) {
        listModel.set(listModel.indexOf(existingEntity), modifiedEntity);
        parentMainFrame.resetErrorLabel();
    }

    @Override
    public void showError(String label, Course entity) {
        parentMainFrame.displayErrorLabel(label + ": " + entity);
    }

    public void setCourseController(CourseController courseController) {
        this.courseController = courseController;
    }

    DefaultListModel<Course> getListModel() {
        return listModel;
    }

    MainFrame getParentMainFrame() {
        return parentMainFrame;
    }

    void setParentMainFrame(MainFrame parentMainFrame) {
        this.parentMainFrame = parentMainFrame;
    }

}
