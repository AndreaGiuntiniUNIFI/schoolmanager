package apt.project.frontend.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import apt.project.backend.domain.Course;
import apt.project.frontend.controller.CustomDialogController;

public class ExamDialog extends CustomDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private CustomDialogController controller;

    private JComboBox<String> examComboBox;

    private JComboBox<String> rateComboBox;

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

        // courses.stream().map(Course::getTitle).forEach(comboBox::addItem);
        // comboBox.setSelectedIndex(-1);
        // contentPanel.add(comboBox);
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

}
