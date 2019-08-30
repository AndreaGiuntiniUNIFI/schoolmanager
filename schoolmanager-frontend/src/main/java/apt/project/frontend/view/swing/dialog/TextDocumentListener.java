package apt.project.frontend.view.swing.dialog;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

interface TextDocumentListener extends DocumentListener {
    @Override
    default void changedUpdate(DocumentEvent e) {
        // Blank default implementation: text components do not fire these
        // events.
        // (See the following link:
        // https://docs.oracle.com/javase/tutorial/uiswing/events/documentlistener.html)
    }
}
