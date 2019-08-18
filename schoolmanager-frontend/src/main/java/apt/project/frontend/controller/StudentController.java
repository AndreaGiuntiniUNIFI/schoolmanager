package apt.project.frontend.controller;

import java.util.List;
import java.util.Map.Entry;
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

        if (!duplicatedExam(modifiedStudent.getExams()).isEmpty()) {
            view.showError("Duplicate Exams in Student: " + modifiedStudent,
                    modifiedStudent);
            return;
        }

        Student studentWithNewName;
        if (!em.catcher(
                () -> ((StudentRepository) repository)
                        .findByName(modifiedStudent.getName()),
                modifiedStudent)) {
            return;
        }
        studentWithNewName = em.getResult();
        if (studentWithNewName != null) {
            view.showError("Already existing entity: " + modifiedStudent,
                    modifiedStudent);
            return;
        }
        super.updateEntity(modifiedStudent);
    }

    public List<Course> duplicatedExam(List<Exam> list) {
        return list.stream()
                .collect(Collectors.groupingBy(Exam::getCourse,
                        Collectors.counting()))
                .entrySet().stream().filter(e -> e.getValue() > 1L)
                .map(Entry<Course, Long>::getKey).collect(Collectors.toList());
    }

}
