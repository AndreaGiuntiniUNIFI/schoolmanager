package apt.project.frontend.view.swing;

import javax.swing.JPanel;

import apt.project.backend.domain.Student;
import apt.project.frontend.view.MainFrame;

public class StudentPanel extends BasePanel<Student> {

    public StudentPanel(JPanel panel, MainFrame parentMainFrame,
            DialogManager dialogManager, String headerText) {

        super(panel, parentMainFrame, dialogManager, headerText);

        btnAdd.addActionListener(e -> {
            String name = dialogManager.manageDialog("Name");
            if (name != null) {
                controller.newEntity(new Student(name));
            }
        });

        btnModify.addActionListener(e -> {
            Student selectedStudent = list.getSelectedValue();
            String name = dialogManager.manageDialog("Name",
                    selectedStudent.getName());
            if (name != null) {
                controller.updateEntity(selectedStudent, new Student(name));
            }
        });

        btnDelete.addActionListener(
                e -> controller.deleteEntity(list.getSelectedValue()));

    }

}
