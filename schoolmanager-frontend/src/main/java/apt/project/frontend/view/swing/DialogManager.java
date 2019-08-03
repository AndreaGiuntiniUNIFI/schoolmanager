package apt.project.frontend.view.swing;

import apt.project.backend.domain.Course;

public class DialogManager {

    public DialogManager() {
    }

    public String manageDialog(String label) {
        CustomDialog dialog = new CustomDialog(label);
        dialog.showDialog();
        return dialog.getOutcome();
    }

    public String manageDialog(String string, Course course) {
        return null;
    }

}
