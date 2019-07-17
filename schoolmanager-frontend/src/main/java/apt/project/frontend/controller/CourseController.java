package apt.project.frontend.controller;

import apt.project.backend.domain.Course;
import apt.project.backend.repository.CourseRepository;
import apt.project.frontend.view.View;

public class CourseController implements Controller {

    private View courseView;
    private CourseRepository courseRepository;

    public CourseController(View courseView,
            CourseRepository courseRepository) {
        this.courseView = courseView;
        this.courseRepository = courseRepository;

    }

    @Override
    public void allEntities() {
        courseView.showAll(courseRepository.findAll());
    }

    public void newEntity(Course course) {
        Course existingCourse = courseRepository.findByTitle(course.getTitle());
        if (existingCourse != null) {
            courseView.showError(
                    "Already existing course with title " + course.getTitle(),
                    course);
            return;
        }
        courseRepository.save(course);
        courseView.entityAdded(course);
    }

    public void deleteEntity(Course courseToDelete) {
        if (courseRepository.findByTitle(courseToDelete.getTitle()) == null) {
            courseView.showError("No existing course with title "
                    + courseToDelete.getTitle(), courseToDelete);
            return;
        }
        courseRepository.deleteByTitle(courseToDelete.getTitle());
        courseView.entityDeleted(courseToDelete);
    }

}
