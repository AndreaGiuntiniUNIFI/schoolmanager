package apt.project.frontend.controller;

import apt.project.backend.domain.Course;
import apt.project.backend.repository.CourseRepository;
import apt.project.frontend.view.View;

public class CourseController extends BaseController<Course> {

    public CourseController(View<Course> view, CourseRepository repository) {
        super(view, repository);
    }

    @Override
    public void newEntity(Course course) {
        Course existingCourse;

        if (!em.catcher(() -> ((CourseRepository) repository)
                .findByTitle(course.getTitle()), course)) {
            return;
        }
        existingCourse = em.getResult();

        if (existingCourse != null) {
            view.showError("Already existing entity: " + course);
            return;
        }
        super.newEntity(course);
    }

    @Override
    public void updateEntity(Course modifiedCourse) {
        Course courseWithNewTitle;

        if (!em.catcher(
                () -> ((CourseRepository) repository)
                        .findByTitle(modifiedCourse.getTitle()),
                modifiedCourse)) {
            return;
        }
        courseWithNewTitle = em.getResult();
        if (courseWithNewTitle != null) {
            view.showError("Already existing entity: " + modifiedCourse);
            return;
        }

        super.updateEntity(modifiedCourse);

    }

}
