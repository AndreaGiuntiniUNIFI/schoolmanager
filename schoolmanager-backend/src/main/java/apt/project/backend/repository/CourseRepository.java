package apt.project.backend.repository;

import java.util.List;
import static java.util.Arrays.asList;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Entity;

public class CourseRepository implements Repository {

    @Override
    public List<Entity> findAll() {
        return asList(new Course("test"));
    }

}
