package apt.project.frontend.controller;

import apt.project.backend.domain.Student;
import apt.project.backend.repository.RepositoryException;
import apt.project.backend.repository.StudentRepository;
import apt.project.frontend.view.View;

public class StudentController implements Controller<Student> {

    private StudentRepository studentRepository;
    private View<Student> studentView;

    @Override
    public void allEntities() {
        ExceptionManager.catcher(() -> {
            studentView.showAll(studentRepository.findAll());
            return null;
        }, studentView);
    }

    @Override
    public void updateEntity(Student existingEntity, Student modifiedEntity) {

        if (ExceptionManager.catcher(
                () -> studentRepository.findById(existingEntity.getId()),
                studentView, existingEntity) == null) {
            studentView.showError(
                    "No existing student with name " + existingEntity.getName(),
                    existingEntity);
            return;
        }

        ExceptionManager.catcher(() -> {
            studentRepository.update(modifiedEntity);
            return null;
        }, studentView, existingEntity);
        studentView.entityUpdated(existingEntity, modifiedEntity);
    }

    @Override
    public void newEntity(Student entity) {

        ExceptionManager.catcher(() -> {
            studentRepository.save(entity);
            return null;
        }, studentView, entity);

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
