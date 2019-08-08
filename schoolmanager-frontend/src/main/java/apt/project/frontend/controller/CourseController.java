package apt.project.frontend.controller;

import apt.project.backend.domain.Course;
import apt.project.backend.repository.CourseRepository;
import apt.project.frontend.view.View;

public class CourseController implements Controller<Course> {

    private View<Course> courseView;
    private CourseRepository courseRepository;

    public CourseController(View<Course> courseView,
            CourseRepository courseRepository) {
        this.courseView = courseView;
        this.courseRepository = courseRepository;

    }

    @Override
    public void allEntities() {
        ExceptionManager.catcher(() -> {
            courseView.showAll(courseRepository.findAll());
            return null;
        }, courseView);
    }

    @Override
    public void newEntity(Course course) {
        Course existingCourse;

        existingCourse = ExceptionManager.catcher(
                () -> courseRepository.findByTitle(course.getTitle()),
                courseView, course);
        if (existingCourse != null) {
            courseView.showError(
                    "Already existing course with title " + course.getTitle(),
                    course);
            return;
        }
        ExceptionManager.catcher(() -> {
            courseRepository.save(course);
            return null;
        }, courseView, course);

        courseView.entityAdded(course);
    }

    @Override
    public void deleteEntity(Course courseToDelete) {
        if (ExceptionManager.catcher(
                () -> courseRepository.findById(courseToDelete.getId()),
                courseView, courseToDelete) == null) {
            courseView.showError("No existing course with title "
                    + courseToDelete.getTitle(), courseToDelete);
            return;
        }
        ExceptionManager.catcher(() -> {
            courseRepository.delete(courseToDelete);
            return null;
        }, courseView, courseToDelete);
        courseView.entityDeleted(courseToDelete);
    }

    @Override
    public void updateEntity(Course existingCourse, Course modifiedCourse) {
        if (ExceptionManager.catcher(
                () -> courseRepository.findById(existingCourse.getId()),
                courseView, existingCourse) == null) {
            courseView.showError("No existing course with title "
                    + existingCourse.getTitle(), existingCourse);
            return;
        }
        ExceptionManager.catcher(() -> {
            courseRepository.update(modifiedCourse);
            return null;
        }, courseView, existingCourse);
        courseView.entityUpdated(existingCourse, modifiedCourse);
    }
}
