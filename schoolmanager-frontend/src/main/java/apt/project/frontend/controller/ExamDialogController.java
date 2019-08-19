package apt.project.frontend.controller;

import java.util.List;

import apt.project.backend.domain.Course;
import apt.project.backend.repository.CourseRepository;
import apt.project.frontend.view.swing.ExamDialog;

public class ExamDialogController {

    private CourseRepository courseRepository;

    private ExamDialog examDialog;

    public ExamDialogController(CourseRepository courseRepository,
            ExamDialog examDialog) {
        this.courseRepository = courseRepository;
        this.examDialog = examDialog;

    }

    public void populateComboBox(List<Course> studentCourses) {
        // TODO Auto-generated method stub
    }

}
