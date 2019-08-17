package apt.project.frontend.view.swing;

import java.util.List;

import apt.project.backend.domain.Exam;

public class DialogManager {

    public DialogManager() {
        //
    }

    public String manageDialog(String label) {
        CustomDialog dialog = new CustomDialog(label);
        dialog.showDialog();
        return dialog.getOutcome();
    }

    public String manageDialog(String label, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    // per questo metodo serve il repository di course e fare la diff con la
    // lista che viene passata (in realta è dialog che deve farlo)
    public Exam manageDialogExam(List<Exam> asList) {
        // TODO Auto-generated method stub
        return null;
    }

}
