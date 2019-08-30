package apt.project.frontend.view.swing;

import javax.swing.JPanel;

import apt.project.backend.domain.Course;
import apt.project.frontend.controller.CourseController;
import apt.project.frontend.view.swing.dialog.DialogManager;

public class CoursePanel extends BasePanel<Course> {

    private CourseController controller;

    public CoursePanel(JPanel panel, DialogManager dialogManager,
            String headerText) {

        super(panel, headerText);

        btnAdd.addActionListener(e -> {
            String title = dialogManager.manageDialog("Title");
            if (title != null) {
                controller.newEntity(new Course(title));
            }
        });

        btnModify.addActionListener(e -> {
            Course selectedCourse = list.getSelectedValue();
            String title = dialogManager.manageDialog("Title",
                    selectedCourse.getTitle());
            if (title != null) {
                selectedCourse.merge(new Course(title));
                controller.updateEntity(selectedCourse);
            }
        });

        btnDelete.addActionListener(
                e -> controller.deleteEntity(list.getSelectedValue()));
    }

    public void setController(CourseController controller) {
        this.controller = controller;
    }

}
