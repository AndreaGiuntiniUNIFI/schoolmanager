package apt.project.frontend.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Entity;
import apt.project.backend.repository.CourseRepository;
import apt.project.frontend.view.View;

public class CourseControllerTest {

    @Mock
    private CourseRepository courseRepository;

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

    @Test
    public void testNewCourseWhenCourseDoesNotAlreadyExist() {
        // setup
        Course course = new Course("Course_1");
        when(courseRepository.findByTitle("Course_1")).thenReturn(null);
        // exercise
        courseController.newEntity(course);
        // verify
        InOrder inOrder = inOrder(courseRepository, courseView);
        inOrder.verify(courseRepository).save(course);
        inOrder.verify(courseView).entityAdded(course);
    }

    @Test
    public void testNewCourseWhenCourseAlreadyExists() {
        // setup
        Course course = new Course("Course_1");
        when(courseRepository.findByTitle("Course_1")).thenReturn(course);
        // exercise
        courseController.newEntity(course);
        // verify
        verify(courseView).showError(
                "Already existing course with title Course_1", course);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }

    @Test
    public void testDeleteCourseWhenCourseExists() {
        // setup
        Course courseToDelete = new Course("Course_1");
        when(courseRepository.findByTitle("Course_1"))
                .thenReturn(courseToDelete);
        // exercise
        courseController.deleteEntity(courseToDelete);
        // verify
        InOrder inOrder = inOrder(courseRepository, courseView);
        inOrder.verify(courseRepository).deleteByTitle("Course_1");
        inOrder.verify(courseView).entityDeleted(courseToDelete);
    }

    @Test
    public void testDeleteCourseWhenCourseDoesNotExists() {
        // setup
        Course courseToDelete = new Course("Course_2");
        when(courseRepository.findByTitle("Course_2")).thenReturn(null);
        // exercise
        courseController.deleteEntity(courseToDelete);
        // verify
        verify(courseView).showError("No existing course with title Course_2",
                courseToDelete);
    }

}
