package apt.project.frontend.controller;

import apt.project.backend.domain.Exam;
import apt.project.backend.domain.Student;
import apt.project.backend.repository.Repository;
import apt.project.backend.repository.RepositoryException;
import apt.project.frontend.view.View;

public class ExamController {

    private View<Exam> view;
    private Repository<Student> repository;
    private ExceptionManager<Student> em;

    public ExamController(View<Exam> view, Repository<Student> repository) {
        this.view = view;
        this.repository = repository;
    }

    public void allEntities(Student student) {
        if (student != null) {
            view.showAll(student.getExams());
        } else {
            view.showError("No existing entity", null);
        }
    }

    public void newEntity(Student student, Exam exam) {
        student.addExam(exam);
        try {
            repository.update(student);
        } catch (RepositoryException e) {
            view.showError(e.getMessage(), exam);
            return;
        }
        view.entityAdded(exam);

    }
}
