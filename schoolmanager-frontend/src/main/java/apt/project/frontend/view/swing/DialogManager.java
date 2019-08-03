package apt.project.frontend.view.swing;

public class DialogManager {

    public DialogManager() {
    }

    public String manageDialog(String label) {
        CustomDialog dialog = new CustomDialog(label);
        dialog.showDialog();
        return dialog.getOutcome();
    }

}
