package apt.project.frontend.controller;

import apt.project.backend.domain.Student;
import apt.project.backend.repository.Repository;
import apt.project.backend.repository.StudentRepository;
import apt.project.frontend.view.View;

public class StudentController extends BaseController<Student> {

    public StudentController(View<Student> view,
            Repository<Student> repository) {
        super(view, repository);
    }

    @Override
    public void newEntity(Student entity) {
        Student existingStudent;
        existingStudent = ExceptionManager
                .catcher(() -> ((StudentRepository) repository)
                        .findByName(entity.getName()), view, entity);
        if (existingStudent != null) {
            view.showError("Already existing entity: " + entity, entity);
            return;
        }
        super.newEntity(entity);
    }

    @Override
    public void updateEntity(Student existingStudent, Student modifiedStudent) {
        Student studentWithNewName;

        studentWithNewName = ExceptionManager.catcher(
                () -> ((StudentRepository) repository)
                        .findByName(modifiedStudent.getName()),
                view, modifiedStudent);
        if (studentWithNewName != null) {
            view.showError("Already existing entity: " + modifiedStudent,
                    modifiedStudent);
            return;
        }
        super.updateEntity(existingStudent, modifiedStudent);
    }

}
