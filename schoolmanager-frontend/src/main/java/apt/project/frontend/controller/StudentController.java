package apt.project.frontend.controller;

import apt.project.backend.domain.Student;
import apt.project.backend.repository.Repository;
import apt.project.frontend.view.View;

public class StudentController extends BaseController<Student> {

    public StudentController(View<Student> view,
            Repository<Student> repository) {
        super(view, repository);
    }

    @Override
    public void newEntity(Student entity) {
        // TODO add check for duplicate name
        super.newEntity(entity);
    }

}
