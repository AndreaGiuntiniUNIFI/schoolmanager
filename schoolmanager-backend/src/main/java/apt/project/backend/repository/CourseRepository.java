package apt.project.backend.repository;

import java.util.List;
import static java.util.Arrays.asList;

import apt.project.backend.domain.Course;

public class CourseRepository {

    public List<Course> findAll() {
        return asList(new Course());
    }

}
