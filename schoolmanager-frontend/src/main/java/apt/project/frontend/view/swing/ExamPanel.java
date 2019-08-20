package apt.project.frontend.view.swing;

import static java.util.stream.Collectors.toList;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Exam;
import apt.project.backend.domain.Student;
import apt.project.frontend.controller.ExamDialogController;
import apt.project.frontend.controller.StudentController;
import apt.project.frontend.view.MainFrame;
import apt.project.frontend.view.swing.dialog.DialogManager;

public class ExamPanel extends BasePanel<Exam> {

    private StudentController controller;
    private ExamDialogController examDialogController;
    private Student student;
    private JButton btnBack;

    public ExamPanel(JPanel examPanel, MainFrame mainFrame,
            DialogManager dialogManager, String headerText) {
        super(examPanel, mainFrame, dialogManager, headerText);

        // TODO: il titolo dei dialog dovrebbe fare riferimento all'esame
        // selezionato
        btnAdd.addActionListener(e -> {
            List<Course> courses = student.getExams().stream()
                    .map(Exam::getCourse).collect(toList());
            Exam exam = dialogManager.manageExamDialog(courses,
                    examDialogController);
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
            String rate = dialogManager.manageSimpleExamDialog();
            if (rate != null) {
                Exam exameToMerge = new Exam();
                exameToMerge.setRate(Integer.parseInt(rate));
                selectedExam.merge(exameToMerge);
                controller.updateEntity(student);
            }
        });

        examPanel.setName("examPanel");
        btnBack = new JButton("Back");
        examPanel.add(btnBack);
        GridBagConstraints gbc_btnBack = new GridBagConstraints();
        gbc_btnBack.insets = new Insets(0, 5, 0, 0);
        gbc_btnBack.gridx = 3;
        gbc_btnBack.gridy = 1;
        examPanel.add(btnBack, gbc_btnBack);

    }

    public void showAll() {
        super.showAll(student.getExams());
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setController(StudentController controller) {
        this.controller = controller;
    }

    public JButton getBtnBack() {
        return btnBack;
    }

    public void setExamDialogController(
            ExamDialogController examDialogController) {
        this.examDialogController = examDialogController;
    }
}
