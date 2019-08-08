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
        try {
            if (studentRepository.findById(existingEntity.getId()) == null) {
                studentView.showError("No existing student with name "
                        + existingEntity.getName(), existingEntity);
                return;
            }
        } catch (RepositoryException e) {
            studentView.showError(e.getMessage(), existingEntity);
            return;
        }
        try {
            studentRepository.update(modifiedEntity);
        } catch (RepositoryException e) {
            studentView.showError(e.getMessage(), existingEntity);
            return;
        }
        studentView.entityUpdated(existingEntity, modifiedEntity);
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
        Student foundEntity = null;
        try {
            foundEntity = studentRepository.findById(entityToDelete.getId());
        } catch (RepositoryException e) {
            studentView.showError(e.getMessage(), entityToDelete);
        }
        if (foundEntity == null) {
            studentView.showError(
                    "No existing student with name " + entityToDelete.getName(),
                    entityToDelete);
            return;
        }
        try {
            studentRepository.delete(entityToDelete);
        } catch (RepositoryException e) {
            studentView.showError(e.getMessage(), entityToDelete);
        }
        studentView.entityDeleted(entityToDelete);

    }

}
