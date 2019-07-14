package apt.project.frontend.view;

import java.util.List;

import apt.project.backend.domain.Course;

public interface View {

    void showAllCourses(List<Course> courses);

}
