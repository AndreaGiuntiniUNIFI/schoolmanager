package apt.project.frontend.view.swing;

import javax.swing.JPanel;

import apt.project.backend.domain.Course;
import apt.project.frontend.view.MainFrame;

public class CoursePanel extends BasePanel<Course> {

    public CoursePanel(JPanel panel, MainFrame parentMainFrame,
            DialogManager dialogManager, String headerText) {

        super(panel, parentMainFrame, dialogManager, headerText);

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
                controller.updateEntity(selectedCourse, new Course(title));
            }
        });

        btnDelete.addActionListener(
                e -> controller.deleteEntity(list.getSelectedValue()));
    }

}
