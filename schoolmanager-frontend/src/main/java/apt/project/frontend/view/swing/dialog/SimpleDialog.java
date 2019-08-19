package apt.project.frontend.view.swing.dialog;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SimpleDialog extends CustomDialog {

    private static final long serialVersionUID = 1L;

    private JTextField textField;

    private String outcome;

    public SimpleDialog(String labelText) {
        super();

        textField = new JTextField(10);
        JLabel label = new JLabel(labelText);

        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                setOutcome(textField.getText());
                setVisible(false);
            }
        });

        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                setOutcome(null);
            }
        });

        textField.getDocument().addDocumentListener(new DocumentListener() {

            private void update() {
                okButton.setEnabled(textField.getText().length() > 0);
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
                update();
            }
        });

        textField.setName(labelText + "TextField");
        contentPanel.add(label);
        contentPanel.add(textField);

    }

    public SimpleDialog(String labelText, String defaultValue) {
        this(labelText);
        this.textField.setText(defaultValue);
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
}
