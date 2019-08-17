package apt.project.frontend.view.swing;

import javax.swing.JPanel;

import apt.project.backend.domain.Exam;
import apt.project.backend.domain.Student;
import apt.project.frontend.controller.StudentController;
import apt.project.frontend.view.MainFrame;

public class ExamPanel extends BasePanel<Exam> {

    private StudentController controller;
    private Student student;

    public ExamPanel(JPanel internalPanel, MainFrame mainFrame,
            DialogManager dialogManager, String headerText) {
        super(internalPanel, mainFrame, dialogManager, headerText);

        btnAdd.addActionListener(e -> {
            Exam exam = dialogManager.manageDialogExam(student.getExams());
            if (exam != null) {
                student.addExam(exam);
                controller.updateEntity(student);
            }
        });

        btnDelete.addActionListener(e -> {
            student.getExams().remove(list.getSelectedIndex());
            controller.updateEntity(student);
        });

        btnModify.addActionListener(e -> {
            Exam selectedExam = list.getSelectedValue();
            String rate = dialogManager.manageDialog("Rate",
                    selectedExam.getRate().toString());
            if (rate != null) {
                Exam exameToMerge = new Exam();
                exameToMerge.setRate(Integer.parseInt(rate));
                selectedExam.merge(exameToMerge);
                controller.updateEntity(student);
            }
        });

    }

    public void showAll() {
        super.showAll(this.student.getExams());
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setController(StudentController controller) {
        this.controller = controller;
    }
}
