package apt.project.frontend.view.swing;

import java.util.List;

import javax.swing.JPanel;

import apt.project.backend.domain.Student;
import apt.project.frontend.view.View;

public class StudentPanel extends JPanel implements View<Student> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the panel.
     */
    public StudentPanel() {

    }

    @Override
    public void showAll(List<Student> entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void entityAdded(Student entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showError(String string, Student entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void entityDeleted(Student entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void entityUpdated(Student existingEntity, Student modifiedEntity) {
        // TODO Auto-generated method stub

    }

}
