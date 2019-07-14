package apt.project.frontend.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;

import apt.project.backend.domain.Course;
import apt.project.backend.repository.CourseRepository;
import apt.project.frontend.view.View;

public class CourseControllerTest {

    


    @Test
    public void testAllCourses() {
        // setup
        // TODO refactor
        CourseRepository courseRepository = mock(CourseRepository.class);
        View courseView = mock(View.class);
        CourseController courseController = new CourseController(courseView, courseRepository);
        List<Course> courses = asList(new Course());
        when(courseRepository.findAll())
        .thenReturn(courses);
        // exercise
        courseController.allCourses();
        // verify
        verify(courseView).showAllCourses(courses);

    }
}
