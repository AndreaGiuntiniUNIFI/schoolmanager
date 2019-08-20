package apt.project.frontend.view.swing.dialog;

import java.util.List;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Exam;
import apt.project.frontend.controller.ExamDialogController;

public class DialogManager {

    public String manageDialog(String label) {
        SimpleDialog dialog = new SimpleDialog(label);
        dialog.showDialog();
        return dialog.getOutcome();
    }

    public String manageDialog(String label, String value) {
        SimpleDialog dialog = new SimpleDialog(label, value);
        dialog.showDialog();
        return dialog.getOutcome();
    }

    public String manageSimpleExamDialog() {
        SimpleExamDialog dialog = new SimpleExamDialog();
        dialog.showDialog();
        return dialog.getOutcome();
    }

    public Exam manageExamDialog(List<Course> courses,
            ExamDialogController controller) {
        ExamDialog dialog = new ExamDialog();
        controller.setExamDialog(dialog);
        controller.populateComboBox(courses);
        dialog.showDialog();
        return dialog.getOutcome();
    }

}
