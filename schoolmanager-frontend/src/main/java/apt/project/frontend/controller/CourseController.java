package apt.project.frontend.controller;

import apt.project.backend.domain.Course;
import apt.project.backend.repository.CourseRepository;
import apt.project.backend.repository.RepositoryException;
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
        try {
            courseView.showAll(courseRepository.findAll());
        } catch (RepositoryException e) {
            courseView.showError(e.getMessage(), null);
        }
    }

    @Override
    public void newEntity(Course course) {
        Course existingCourse;
        try {
            existingCourse = courseRepository.findByTitle(course.getTitle());
            if (existingCourse != null) {
                courseView.showError("Already existing course with title "
                        + course.getTitle(), course);
                return;
            }
            courseRepository.save(course);
        } catch (RepositoryException e) {
            courseView.showError(e.getMessage(), course);
        }

        courseView.entityAdded(course);
    }

    @Override
    public void deleteEntity(Course courseToDelete) {
        try {
            if (courseRepository.findById(courseToDelete.getId()) == null) {
                courseView.showError("No existing course with title "
                        + courseToDelete.getTitle(), courseToDelete);
                return;
            }
            courseRepository.delete(courseToDelete);
        } catch (RepositoryException e) {
            courseView.showError(e.getMessage(), courseToDelete);
        }
        courseView.entityDeleted(courseToDelete);
    }

    @Override
    public void updateEntity(Course existingCourse, Course modifiedCourse) {
        try {
            if (courseRepository.findById(existingCourse.getId()) == null) {
                courseView.showError("No existing course with title "
                        + existingCourse.getTitle(), existingCourse);
                return;
            }
            courseRepository.update(modifiedCourse);
        } catch (RepositoryException e) {
            courseView.showError(e.getMessage(), existingCourse);
        }
        courseView.entityUpdated(existingCourse, modifiedCourse);
    }

}
