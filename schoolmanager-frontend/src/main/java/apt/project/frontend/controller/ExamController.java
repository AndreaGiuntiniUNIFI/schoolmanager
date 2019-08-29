package apt.project.frontend.controller;

import java.util.List;

import apt.project.backend.domain.Exam;
import apt.project.backend.domain.Student;
import apt.project.backend.repository.Repository;
import apt.project.backend.repository.RepositoryException;
import apt.project.backend.repository.StudentRepository;
import apt.project.frontend.view.View;

public class ExamController {

    private static final String EXAM = "Exam: ";
    private View<Exam> view;
    private Repository<Student> repository;
    private ExceptionManager<Exam> em;

    public ExamController(View<Exam> view, Repository<Student> repository) {
        this.view = view;
        this.repository = repository;
        this.em = new ExceptionManager<>(view);
    }

    public void allEntities(Student student) {
        List<Exam> exams = null;
        if (student == null) {
            view.showError("Student is null");
            return;
        }
        try {
            exams = ((StudentRepository) repository)
                    .getAllExams(student.getId());
        } catch (RepositoryException e) {
            view.showError(e.getMessage());
            return;
        }
        student.setExams(exams);
        view.showAll(exams);
    }

    public void newEntity(Student student, Exam exam) {
        if (student.findExam(exam) != -1) {
            view.showError(EXAM + exam + " already registered in student");
            return;
        }
        student.addExam(exam);
        if (!em.voidCatcher(() -> repository.update(student), exam)) {
            student.removeExam(exam);
            return;
        }
        view.entityAdded(exam);
    }

    public void deleteEntity(Student student, Exam exam) {
        if (student.findExam(exam) == -1) {
            view.showError(EXAM + exam + " not registered in student");
            return;
        }
        student.removeExam(exam);
        if (!em.voidCatcher(() -> repository.update(student), exam)) {
            student.addExam(exam);
            return;
        }
        view.entityDeleted(exam);
    }

    public void updateEntity(Student student, Exam exam) {
        int index = student.findExam(exam);
        if (index == -1) {
            view.showError(EXAM + exam + " not registered in student");
            return;
        }
        Exam oldExam = student.getExams().get(index);
        student.getExams().set(index, exam);
        if (!em.voidCatcher(() -> repository.update(student), exam)) {
            student.getExams().set(index, oldExam);
            return;
        }
        view.entityUpdated(exam);
    }

}
