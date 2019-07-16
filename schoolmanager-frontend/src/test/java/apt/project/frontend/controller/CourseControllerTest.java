package apt.project.frontend.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Entity;
import apt.project.backend.repository.Repository;
import apt.project.frontend.view.View;

public class CourseControllerTest {

    @Mock
    private Repository courseRepository;

    @Mock
    private View courseView;

    @InjectMocks
    private CourseController courseController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAllCourses() {
        List<Entity> courses = asList(new Course("test"));
        when(courseRepository.findAll()).thenReturn(courses);
        // exercise
        courseController.allEntities();
        // verify
        verify(courseView).showAll(courses);
    }
}
