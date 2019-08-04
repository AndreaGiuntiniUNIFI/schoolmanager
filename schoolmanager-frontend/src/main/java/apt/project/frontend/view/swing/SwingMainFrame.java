package apt.project.frontend.view.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apt.project.frontend.view.MainFrame;

public class SwingMainFrame extends JFrame implements MainFrame {

    private static final Logger LOGGER = LogManager
            .getLogger(SwingMainFrame.class);

    private static final long serialVersionUID = 1L;

    private JPanel contentPane;

    private JLabel errorLabel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                SwingMainFrame frame = new SwingMainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
    }

    /**
     * Create the frame.
     */
    public SwingMainFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setName("mainTabbedPane");
        contentPane.add(tabbedPane, BorderLayout.NORTH);

        JPanel studentPanel = new StudentPanel();
        studentPanel.setName("studentPanel");
        tabbedPane.addTab("Students", null, studentPanel, null);

        DialogManager dialogManager = new DialogManager();
        JPanel coursePanel = new CoursePanel(this, dialogManager);
        coursePanel.setName("coursePanel");
        tabbedPane.addTab("Courses", null, coursePanel, null);

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
