package apt.project.frontend.view.swing;

import javax.swing.JPanel;

import apt.project.backend.domain.Course;
import apt.project.frontend.controller.CourseController;
import apt.project.frontend.view.MainFrame;

public class CoursePanel extends BasePanel<Course> {

    private CourseController controller;

    public CoursePanel(JPanel panel, MainFrame parentMainFrame,
            DialogManager dialogManager, String headerText) {

        super(panel, parentMainFrame, dialogManager, headerText);

        btnAdd.addActionListener(e -> {
            String title = dialogManager.manageDialog("Title");
            if (title != null) {
                getController().newEntity(new Course(title));
            }
        });

        btnModify.addActionListener(e -> {
            Course selectedCourse = list.getSelectedValue();
            String title = dialogManager.manageDialog("Title",
                    selectedCourse.getTitle());
            if (title != null) {
                selectedCourse.merge(new Course(title));
                getController().updateEntity(selectedCourse);
            }
        });

        btnDelete.addActionListener(
                e -> getController().deleteEntity(list.getSelectedValue()));
    }

    public CourseController getController() {
        return controller;
    }

    public void setController(CourseController controller) {
        this.controller = controller;
    }

}
