package apt.project.frontend.controller;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
import apt.project.backend.repository.CourseRepository;
import apt.project.backend.repository.RepositoryException;
import apt.project.frontend.view.View;

public class CourseControllerTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private View<Course> courseView;

    @InjectMocks
    private CourseController courseController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAllCourses() throws RepositoryException {
        // setup
        List<Course> courses = asList(new Course("test"));
        when(courseRepository.findAll()).thenReturn(courses);
        // exercise
        courseController.allEntities();
        // verify
        verify(courseView).showAll(courses);
    }

    @Test
    public void testAllCoursesWhenRepositoryExceptionIsThrownInFindAllShouldShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        when(courseRepository.findAll())
                .thenThrow(new RepositoryException(message));
        // exercise
        courseController.allEntities();
        // verify
        verify(courseView).showError("Repository exception: " + message, null);
    }

    @Test
    public void testNewCourseWhenCourseDoesNotAlreadyExist()
            throws RepositoryException {
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
    public void testNewCourseWhenCourseAlreadyExists()
            throws RepositoryException {
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
    public void testNewCourseWhenRepositoryExceptionIsThrownInFindByTitleShouldShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        Course course = new Course("Course_1");
        when(courseRepository.findByTitle("Course_1"))
                .thenThrow(new RepositoryException(message));
        // exercise
        courseController.newEntity(course);
        // verify
        verify(courseView).showError("Repository exception: " + message,
                course);
    }

    @Test
    public void testNewCourseWhenRepositoryExceptionIsThrownInSaveShouldShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        Course course = new Course("Course_1");
        doThrow(new RepositoryException(message)).when(courseRepository)
                .save(course);
        // exercise
        courseController.newEntity(course);
        // verify
        verify(courseView).showError("Repository exception: " + message,
                course);
    }

    @Test
    public void testDeleteCourseWhenCourseExists() throws RepositoryException {
        // setup
        Course courseToDelete = new Course("Course_1");
        when(courseRepository.findById((Long) any()))
                .thenReturn(courseToDelete);
        // exercise
        courseController.deleteEntity(courseToDelete);
        // verify
        InOrder inOrder = inOrder(courseRepository, courseView);
        inOrder.verify(courseRepository).delete(courseToDelete);
        inOrder.verify(courseView).entityDeleted(courseToDelete);
    }

    @Test
    public void testDeleteCourseWhenCourseDoesNotExists()
            throws RepositoryException {
        // setup
        Course courseToDelete = new Course("Course_1");
        when(courseRepository.findById((Long) any())).thenReturn(null);
        // exercise
        courseController.deleteEntity(courseToDelete);
        // verify
        verify(courseView).showError("No existing course with title Course_1",
                courseToDelete);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }

    @Test
    public void testDeleteCourseWhenRepositoryExceptionIsThrownInFindByIdShouldShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        Course courseToDelete = new Course("Course_1");
        when(courseRepository.findById((Long) any()))
                .thenThrow(new RepositoryException(message));
        // exercise
        courseController.deleteEntity(courseToDelete);
        // verify
        verify(courseView).showError("Repository exception: " + message,
                courseToDelete);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }

    @Test
    public void testDeleteCourseWhenRepositoryExceptionIsThrownInDeleteShouldShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        Course courseToDelete = new Course("Course_1");
        when(courseRepository.findById((Long) any()))
                .thenReturn(courseToDelete);
        doThrow(new RepositoryException(message)).when(courseRepository)
                .delete(courseToDelete);
        // exercise
        courseController.deleteEntity(courseToDelete);
        // verify
        verify(courseView).showError("Repository exception: " + message,
                courseToDelete);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }

    @Test
    public void testUpdateCourseWhenCourseExists() throws RepositoryException {
        // setup
        Course existingCourse = new Course("existingTitle");
        Course modifiedCourse = new Course("modifiedTitle");
        when(courseRepository.findById((Long) any()))
                .thenReturn(existingCourse);
        // exercise
        courseController.updateEntity(existingCourse, modifiedCourse);
        // verify
        InOrder inOrder = inOrder(courseRepository, courseView);
        inOrder.verify(courseRepository).update(modifiedCourse);
        inOrder.verify(courseView).entityUpdated(existingCourse,
                modifiedCourse);
    }

    @Test
    public void testUpdateCourseWhenCourseDoesNotExist()
            throws RepositoryException {
        // setup
        Course existingCourse = new Course("existingTitle");
        Course modifiedCourse = new Course("modifiedTitle");
        when(courseRepository.findById((Long) any())).thenReturn(null);
        // exercise
        courseController.updateEntity(existingCourse, modifiedCourse);
        // verify
        verify(courseView).showError(
                "No existing course with title existingTitle", existingCourse);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }

    @Test
    public void testUpdateCourseWhenRepositoryExceptionIsThrownInFindByIdShouldShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        Course existingCourse = new Course("existingTitle");
        Course modifiedCourse = new Course("modifiedTitle");
        when(courseRepository.findById((Long) any()))
                .thenThrow(new RepositoryException(message));
        // exercise
        courseController.updateEntity(existingCourse, modifiedCourse);
        // verify
        verify(courseView).showError("Repository exception: " + message,
                existingCourse);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }

    @Test
    public void testUpdateCourseWhenRepositoryExceptionIsThrownUpdateShouldShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        Course existingCourse = new Course("existingTitle");
        Course modifiedCourse = new Course("modifiedTitle");
        when(courseRepository.findById((Long) any()))
                .thenReturn(existingCourse);
        doThrow(new RepositoryException(message)).when(courseRepository)
                .update(modifiedCourse);
        when(courseRepository.findById((Long) any()))
                .thenThrow(new RepositoryException(message));
        // exercise
        courseController.updateEntity(existingCourse, modifiedCourse);
        // verify
        verify(courseView).showError("Repository exception: " + message,
                existingCourse);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }
}
