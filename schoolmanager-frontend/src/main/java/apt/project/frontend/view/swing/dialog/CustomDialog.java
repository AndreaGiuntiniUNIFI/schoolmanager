package apt.project.frontend.view.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomDialog extends JDialog {

    private static final Logger LOGGER = LogManager
            .getLogger(CustomDialog.class);

    private static final long serialVersionUID = 1L;

    protected final JPanel contentPanel = new JPanel();

    protected JButton okButton;

    protected JButton cancelButton;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            CustomDialog dialog = new CustomDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    /**
     * Create the dialog.
     */

    public CustomDialog() {
        super();
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        setBounds(100, 100, 450, 300);
        setLayout(new BorderLayout());

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        add(buttonPane, BorderLayout.SOUTH);

        okButton = new JButton("OK");
        okButton.setEnabled(false);
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

        cancelButton = new JButton("Cancel");
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                setVisible(false);
            }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);

        contentPanel.setLayout(new FlowLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        add(contentPanel, BorderLayout.CENTER);
    }

    public void showDialog() {
        pack();
        setVisible(true);
    }

}
