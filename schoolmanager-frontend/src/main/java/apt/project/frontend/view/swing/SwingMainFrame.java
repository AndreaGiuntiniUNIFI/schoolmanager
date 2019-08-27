package apt.project.frontend.view.swing;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import apt.project.frontend.view.MainFrame;

public class SwingMainFrame extends JFrame implements MainFrame {

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;

    private JLabel errorLabel;

    public SwingMainFrame(CoursePanel coursePanel, StudentPanel studentPanel) {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
        tabbedPane.setName("mainTabbedPane");
        contentPane.add(tabbedPane, BorderLayout.NORTH);

        studentPanel.getPanel().setName("studentPanel");
        tabbedPane.addTab("Students", null, studentPanel.getCardsPanel(), null);

        coursePanel.getPanel().setName("coursePanel");
        tabbedPane.addTab("Courses", null, coursePanel.getPanel(), null);

        errorLabel = new JLabel(" ");
        errorLabel.setName("errorLabel");
        contentPane.add(errorLabel, BorderLayout.SOUTH);

    }

    @Override
    public void displayErrorLabel(String message) {
        errorLabel.setText(message);
    }

    @Override
    public void resetErrorLabel() {
        errorLabel.setText(" ");
    }

    public JLabel getErrorLabel() {
        return errorLabel;
    }

}
