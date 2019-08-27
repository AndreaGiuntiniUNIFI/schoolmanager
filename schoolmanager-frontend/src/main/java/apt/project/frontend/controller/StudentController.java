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
        if (!em.catcher(() -> ((StudentRepository) repository)
                .findByName(entity.getName()), entity)) {
            return;
        }
        existingStudent = em.getResult();
        if (existingStudent != null) {
            view.showError("Already existing entity: " + entity, entity);
            return;
        }
        super.newEntity(entity);
    }

    @Override
    public void updateEntity(Student modifiedStudent) {

        Student studentWithNewName;
        if (!em.catcher(
                () -> ((StudentRepository) repository)
                        .findByName(modifiedStudent.getName()),
                modifiedStudent)) {
            return;
        }
        studentWithNewName = em.getResult();

        if (studentWithNewName != null) {
            if (!studentWithNewName.getId().equals(modifiedStudent.getId())) {
                view.showError("Already existing student with this name: "
                        + modifiedStudent, modifiedStudent);
            }
            return;
        }
        super.updateEntity(modifiedStudent);
    }

}
