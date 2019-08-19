package apt.project.frontend.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Exam;
import apt.project.frontend.controller.CustomDialogController;

public class ExamDialog extends CustomDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private CustomDialogController controller;

    private JComboBox<String> examComboBox;

    private JComboBox<String> rateComboBox;

    private List<Course> courses;

    private Exam outcome;

    public ExamDialog() {
        super();
        JLabel examLabel = new JLabel("Exam");
        JLabel rateLabel = new JLabel("Rate");
        contentPanel.add(examLabel);
        contentPanel.add(rateLabel);

        ActionListener btnOkEnabler = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okButton.setEnabled(rateComboBox.getSelectedIndex() >= 0
                        && examComboBox.getSelectedIndex() >= 0);
            }
        };

        examComboBox = new JComboBox<>();
        getExamComboBox().setName("examComboBox");
        examComboBox.addActionListener(btnOkEnabler);
        contentPanel.add(getExamComboBox());

        rateComboBox = new JComboBox<>();
        getRateComboBox().setName("rateComboBox");
        rateComboBox.addActionListener(btnOkEnabler);
        contentPanel.add(getRateComboBox());

        IntStream.range(18, 31).boxed()
                .forEach(i -> rateComboBox.addItem(i.toString()));

        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                String rate = (String) rateComboBox.getSelectedItem();
                outcome = new Exam(courses.get(examComboBox.getSelectedIndex()),
                        Integer.parseInt(rate));

                setVisible(false);
            }
        });

        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                setOutcome(null);
            }
        });
    }

    public void setController(CustomDialogController controller) {
        this.controller = controller;
    }

    public void initializeComboBox(List<Course> courses) {
        controller.populateComboBox(courses);
    }

    JComboBox<String> getExamComboBox() {
        return examComboBox;
    }

    JComboBox<String> getRateComboBox() {
        return rateComboBox;
    }

    public Exam getOutcome() {
        return outcome;
    }

    public void setOutcome(Exam outcome) {
        this.outcome = outcome;
    }

    public void setCoursesComboBox(List<Course> courses) {
        this.courses = courses;
        this.courses.stream()
                .forEach(course -> examComboBox.addItem(course.toString()));
    }

    List<Course> getCourses() {
        return courses;
    }

    void setCourses(List<Course> courses) {
        this.courses = courses;
    }

}
