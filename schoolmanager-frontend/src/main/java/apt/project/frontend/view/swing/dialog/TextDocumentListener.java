package apt.project.frontend.view.swing.dialog;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

interface TextDocumentListener extends DocumentListener {
    @Override
    default void changedUpdate(DocumentEvent e) {
    }
}
