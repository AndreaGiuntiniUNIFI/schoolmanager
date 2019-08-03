package apt.project.frontend.view.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import apt.project.backend.domain.Course;
import apt.project.frontend.controller.CourseController;
import apt.project.frontend.view.View;

public class CoursePanel extends JPanel implements View<Course> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private CourseController courseController;

    /**
     * Create the panel.
     * 
     * @param courseController
     * 
     * @param dialogManager
     */
    public CoursePanel(DialogManager dialogManager) {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 0.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridwidth = 3;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        add(scrollPane, gbc_scrollPane);

        JList list = new JList();
        list.setName("coursesList");
        scrollPane.setViewportView(list);

        JLabel lblListOfCourses = new JLabel("List of Courses");
        lblListOfCourses.setHorizontalAlignment(SwingConstants.CENTER);
        scrollPane.setColumnHeaderView(lblListOfCourses);

        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(e -> {
            String title = dialogManager.manageDialog("Title");
            courseController.newEntity(new Course(title));

        });

        GridBagConstraints gbc_btnAdd = new GridBagConstraints();
        gbc_btnAdd.insets = new Insets(0, 0, 0, 5);
        gbc_btnAdd.gridx = 0;
        gbc_btnAdd.gridy = 1;
        add(btnAdd, gbc_btnAdd);

        JButton btnModify = new JButton("Modify");
        btnModify.setEnabled(false);
        GridBagConstraints gbc_btnModify = new GridBagConstraints();
        gbc_btnModify.insets = new Insets(0, 0, 0, 5);
        gbc_btnModify.gridx = 1;
        gbc_btnModify.gridy = 1;
        add(btnModify, gbc_btnModify);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setEnabled(false);
        GridBagConstraints gbc_btnDelete = new GridBagConstraints();
        gbc_btnDelete.gridx = 2;
        gbc_btnDelete.gridy = 1;
        add(btnDelete, gbc_btnDelete);

    }

    @Override
    public void showAll(List<Course> entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void entityAdded(Course entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showError(String string, Course entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void entityDeleted(Course entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void entityUpdated(Course existingEntity, Course modifiedEntity) {
        // TODO Auto-generated method stub

    }

    public void setCourseController(CourseController courseController) {
        this.courseController = courseController;
    }

}
