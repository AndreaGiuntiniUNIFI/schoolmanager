package apt.project.frontend.controller;

import apt.project.backend.repository.CourseRepository;
import apt.project.frontend.view.View;

public class CourseController {

    private View courseView;
    private CourseRepository courseRepository;

    public CourseController(View courseView, CourseRepository courseRepository) {
        this.courseView = courseView;
        this.courseRepository = courseRepository;
    }

    public void allCourses() {
         courseView.showAllCourses(courseRepository.findAll());
    }

}
