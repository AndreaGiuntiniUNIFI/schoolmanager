package apt.project.frontend.controller;

import java.util.List;

import apt.project.backend.domain.Student;
import apt.project.backend.repository.RepositoryException;
import apt.project.backend.repository.StudentRepository;
import apt.project.frontend.view.View;

public class StudentController implements Controller<Student> {

    private StudentRepository studentRepository;
    private View<Student> studentView;

    @Override
    public void allEntities() {
        List<Student> studentList = null;
        try {
            studentList = studentRepository.findAll();
        } catch (RepositoryException e) {
            studentView.showError(e.getMessage(), null);
        }
        studentView.showAll(studentList);
    }

    @Override
    public void updateEntity(Student existingEntity, Student modifiedEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void newEntity(Student entity) {

        try {
            studentRepository.save(entity);
        } catch (RepositoryException e) {
            studentView.showError(e.getMessage(), entity);
        }
        studentView.entityAdded(entity);
    }

    @Override
    public void deleteEntity(Student entityToDelete) {
        // TODO Auto-generated method stub

    }

}
