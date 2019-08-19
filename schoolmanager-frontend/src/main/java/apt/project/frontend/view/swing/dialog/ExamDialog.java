package apt.project.frontend.view.swing.dialog;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Exam;

public class ExamDialog extends CustomDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private JComboBox<String> examComboBox;

    private JComboBox<String> rateComboBox;

    private transient List<Course> courses;

    private transient Exam outcome;

    public ExamDialog() {
        super();
        JLabel examLabel = new JLabel("Exam");
        JLabel rateLabel = new JLabel("Rate");

        ActionListener btnOkEnabler = e -> okButton
                .setEnabled(rateComboBox.getSelectedIndex() >= 0
                        && examComboBox.getSelectedIndex() >= 0);

        examComboBox = new JComboBox<>();
        examComboBox.setName("examComboBox");
        examComboBox.addActionListener(btnOkEnabler);

        rateComboBox = new JComboBox<>();
        rateComboBox.setName("rateComboBox");
        rateComboBox.addActionListener(btnOkEnabler);

        IntStream.range(18, 31).boxed().map(String::valueOf)
                .forEach(rateComboBox::addItem);

        rateComboBox.setSelectedIndex(-1);
        examComboBox.setSelectedIndex(-1);

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
                outcome = null;
            }
        });

        contentPanel.add(examLabel);
        contentPanel.add(examComboBox);
        contentPanel.add(rateLabel);
        contentPanel.add(rateComboBox);
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
        this.courses.stream().map(Course::toString)
                .forEach(examComboBox::addItem);
    }

    List<Course> getCourses() {
        return courses;
    }

    void setCourses(List<Course> courses) {
        this.courses = courses;
    }

}
