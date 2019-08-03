package apt.project.frontend.view.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class CustomDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String outcome;

    private final JPanel contentPanel = new JPanel();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            CustomDialog dialog = new CustomDialog("Test");
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public CustomDialog(String labelText) {
        super();
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        JTextField textField = new JTextField(10);

        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton okButton = new JButton("OK");
        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                setOutcome(textField.getText());
                setVisible(false);
            }
        });
        okButton.setEnabled(false);
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                outcome = null;
                setVisible(false);
            }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);

        contentPanel.setLayout(new FlowLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel label = new JLabel(labelText);
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
            }
        });

        textField.setName(labelText + "TextField");
        contentPanel.add(label);
        contentPanel.add(textField);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

    }

    public void showDialog() {
        pack();
        setVisible(true);
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

}
