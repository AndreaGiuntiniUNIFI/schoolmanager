package apt.project.frontend.view.swing.dialog;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.stream.IntStream;

import javax.swing.JComboBox;
import javax.swing.JLabel;

public class SimpleExamDialog extends CustomDialog {

    private static final long serialVersionUID = 1L;

    private JComboBox<String> rateComboBox;

    private String outcome;

    public SimpleExamDialog() {
        super();

        JLabel rateLabel = new JLabel("Rate");

        rateComboBox = new JComboBox<>();
        rateComboBox.setName("rateComboBox");
        rateComboBox.addActionListener(
                e -> okButton.setEnabled(rateComboBox.getSelectedIndex() >= 0));
        IntStream.range(18, 31).boxed().map(String::valueOf)
                .forEach(rateComboBox::addItem);
        rateComboBox.setSelectedIndex(-1);

        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                outcome = (String) rateComboBox.getSelectedItem();
                setVisible(false);
            }
        });

        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                outcome = null;
            }
        });

        contentPanel.add(rateLabel);
        contentPanel.add(rateComboBox);
    }

    public String getOutcome() {
        return outcome;
    }

}
