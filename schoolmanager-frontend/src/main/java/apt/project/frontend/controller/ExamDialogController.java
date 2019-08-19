package apt.project.frontend.controller;

import static java.util.stream.Collectors.toList;

import java.util.List;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Exam;
import apt.project.backend.repository.CourseRepository;
import apt.project.backend.repository.RepositoryException;
import apt.project.frontend.view.View;
import apt.project.frontend.view.swing.dialog.ExamDialog;

public class ExamDialogController {

    private View<Exam> view;

    private CourseRepository courseRepository;

    private ExamDialog examDialog;

    public ExamDialogController(View<Exam> view,
            CourseRepository courseRepository, ExamDialog examDialog) {
        this.view = view;
        this.courseRepository = courseRepository;
        this.examDialog = examDialog;
    }

    public boolean populateComboBox(List<Course> studentCourses) {
        List<Course> coursesInRepository;
        try {
            coursesInRepository = courseRepository.findAll();
        } catch (RepositoryException e) {
            view.showError(e.getMessage(), null);
            return false;
        }

        if (coursesInRepository.isEmpty()) {
            view.showError("Cannot add an exam. No course existing.", null);
            return false;
        }

        List<Course> difference = coursesInRepository.stream()
                .filter(course -> !studentCourses.contains(course))
                .collect(toList());

        if (difference.isEmpty()) {
            view.showError("Cannot add an exam. Alredy registered all exams.",
                    null);
            return false;
        }

        examDialog.setCoursesComboBox(difference);
        return true;
    }

}
