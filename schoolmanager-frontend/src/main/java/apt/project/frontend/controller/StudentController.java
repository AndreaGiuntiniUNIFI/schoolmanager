package apt.project.frontend.controller;

import java.util.List;
import java.util.stream.Collectors;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Exam;
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

        if (duplicatedExam(modifiedStudent.getExams()).size() > 0) {
            view.showError("Duplicate Exams in Student: " + modifiedStudent,
                    modifiedStudent);
            return;
        }
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

    public List<Course> duplicatedExam(List<Exam> list) {
        return list.stream()
                .collect(Collectors.groupingBy(Exam::getCourse,
                        Collectors.counting()))
                .entrySet().stream().filter(e -> e.getValue() > 1L)
                .map(e -> e.getKey()).collect(Collectors.toList());
    }

}
