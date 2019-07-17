package apt.project.backend.repository;

import static java.util.Arrays.asList;

import java.util.List;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Entity;

public class CourseRepository implements Repository {

    @Override
    public List<Entity> findAll() {
        return asList(new Course("test"));
    }

    @Override
    public void save(Entity e) {
        return;
    }

    @Override
    public void delete(Entity e) {
        // TODO Auto-generated method stub

    }

    public Course findByTitle(String titleToFind) {
        return null;
    }

    public void deleteByTitle(String titleToDelete) {
        return;
    }

}
