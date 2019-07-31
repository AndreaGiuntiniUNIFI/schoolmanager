package apt.project.frontend.view.swing;

import static java.util.Arrays.asList;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import apt.project.backend.domain.Course;
import apt.project.frontend.view.View;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CoursePanel extends JPanel implements View<Course> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the panel.
     */
    public CoursePanel() {
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
        btnAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                getCustomInputDialog("Title", "Add Course");
            }
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

    public String getCustomInputDialog(String field, String title) {

        JPanel panel = new JPanel();

        JLabel label = new JLabel(field);
        panel.add(label);

        JButton okBtn = new JButton("OK");
        okBtn.setEnabled(false);
        panel.add(okBtn);

        JButton cancelBtn = new JButton("Cancel");
        panel.add(cancelBtn);

        JTextField textField = new JTextField(10);
        textField.setName(field + "TxtField");
        textField.getDocument().addDocumentListener(new DocumentListener() {

            private void update() {
                okBtn.setEnabled(textField.getText().length() > 0);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        panel.add(textField);

        int choice = JOptionPane.showOptionDialog(null, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                new JButton[] { okBtn, cancelBtn }, okBtn);

        return " ";

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

}
