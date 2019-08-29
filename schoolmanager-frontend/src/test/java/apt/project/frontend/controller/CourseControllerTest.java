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
    public void testAllEntitiesShouldCallView() throws RepositoryException {
        // setup
        List<Course> courses = asList(new Course("test"));
        when(courseRepository.findAll()).thenReturn(courses);
        // exercise
        courseController.allEntities();
        // verify
        verify(courseView).showAll(courses);
    }

    @Test
    public void testAllEntitiesWhenRepositoryExceptionIsThrownInFindAllShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        when(courseRepository.findAll())
                .thenThrow(new RepositoryException(message));
        // exercise
        courseController.allEntities();
        // verify
        verify(courseView).showError("Repository exception: message, " + null);
    }

    @Test
    public void testNewEntityWhenEntityDoesNotAlreadyExist()
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
    public void testNewEntityWhenEntityAlreadyExists()
            throws RepositoryException {
        // setup
        Course course = new Course("Course_1");
        when(courseRepository.findByTitle("Course_1")).thenReturn(course);
        // exercise
        courseController.newEntity(course);
        // verify
        verify(courseView).showError("Already existing entity: " + course);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }

    @Test
    public void testNewEntityWhenRepositoryExceptionIsThrownInFindByTitleShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        Course course = new Course("Course_1");
        when(courseRepository.findByTitle("Course_1"))
                .thenThrow(new RepositoryException(message));
        // exercise
        courseController.newEntity(course);
        // verify
        verify(courseView)
                .showError("Repository exception: message, " + course);
    }

    @Test
    public void testNewEntityWhenRepositoryExceptionIsThrownInSaveShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        Course course = new Course("Course_1");
        doThrow(new RepositoryException(message)).when(courseRepository)
                .save(course);
        // exercise
        courseController.newEntity(course);
        // verify
        verify(courseView)
                .showError("Repository exception: message, " + course);
    }

    @Test
    public void testDeleteEntityWhenEntityExists() throws RepositoryException {
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
    public void testDeleteEntityWhenEntityDoesNotExist()
            throws RepositoryException {
        // setup
        Course courseToDelete = new Course("Course_1");
        when(courseRepository.findById((Long) any())).thenReturn(null);
        // exercise
        courseController.deleteEntity(courseToDelete);
        // verify
        verify(courseView).showError("No existing entity: " + courseToDelete);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }

    @Test
    public void testDeleteEntityWhenRepositoryExceptionIsThrownInFindByIdShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        Course courseToDelete = new Course("Course_1");
        when(courseRepository.findById((Long) any()))
                .thenThrow(new RepositoryException(message));
        // exercise
        courseController.deleteEntity(courseToDelete);
        // verify
        verify(courseView)
                .showError("Repository exception: message, " + courseToDelete);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }

    @Test
    public void testDeleteEntityWhenRepositoryExceptionIsThrownInDeleteShouldCallShowError()
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
        verify(courseView)
                .showError("Repository exception: message, " + courseToDelete);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }

    @Test
    public void testUpdateEntityWhenEntityExists() throws RepositoryException {
        // setup
        Course existingCourse = new Course("existingTitle");
        Course modifiedCourse = new Course("modifiedTitle");
        when(courseRepository.findById((Long) any()))
                .thenReturn(existingCourse);
        // exercise
        courseController.updateEntity(modifiedCourse);
        // verify
        InOrder inOrder = inOrder(courseRepository, courseView);
        inOrder.verify(courseRepository).update(modifiedCourse);
        inOrder.verify(courseView).entityUpdated(modifiedCourse);
    }

    @Test
    public void testUpdateEntityWhenEntityDoesNotExist()
            throws RepositoryException {
        // setup
        Course modifiedCourse = new Course("modifiedTitle");
        when(courseRepository.findByTitle("modifiedTitle")).thenReturn(null);
        when(courseRepository.findById((Long) any())).thenReturn(null);
        // exercise
        courseController.updateEntity(modifiedCourse);
        // verify
        verify(courseView).showError("No existing entity: " + modifiedCourse);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }

    @Test
    public void testUpdateEntityWhenRepositoryExceptionIsThrownInFindByIdShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        Course modifiedCourse = new Course("modifiedTitle");
        when(courseRepository.findByTitle("modifiedTitle")).thenReturn(null);
        when(courseRepository.findById((Long) any()))
                .thenThrow(new RepositoryException(message));
        // exercise
        courseController.updateEntity(modifiedCourse);
        // verify
        verify(courseView)
                .showError("Repository exception: message, " + modifiedCourse);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }

    @Test
    public void testUpdateEntityWhenRepositoryExceptionIsThrownInUpdateShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        Course existingCourse = new Course("existingTitle");
        Course modifiedCourse = new Course("modifiedTitle");
        when(courseRepository.findById((Long) any()))
                .thenReturn(existingCourse);
        when(courseRepository.findByTitle("modifiedTitle")).thenReturn(null);
        doThrow(new RepositoryException(message)).when(courseRepository)
                .update(modifiedCourse);
        // exercise
        courseController.updateEntity(modifiedCourse);
        // verify
        verify(courseView)
                .showError("Repository exception: message, " + modifiedCourse);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }

    @Test
    public void testUpdateEntityWhenModifiedTitleAlreadyExist()
            throws RepositoryException {
        // setup

        String modifiedTitle = "modifiedTitle";
        Course modifiedCourse = new Course(modifiedTitle);
        when(courseRepository.findByTitle(modifiedTitle))
                .thenReturn(new Course(modifiedTitle));

        // exercise
        courseController.updateEntity(modifiedCourse);
        // verify
        verify(courseView)
                .showError("Already existing entity: " + modifiedCourse);
        verifyNoMoreInteractions(ignoreStubs(courseRepository));
    }

    @Test
    public void testUpdateEntityWhenRepositoryExceptionIsThrownInFindByTitleShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        String modifiedTitle = "modifiedTitle";
        Course modifiedCourse = new Course(modifiedTitle);

        when(courseRepository.findByTitle(modifiedTitle))
                .thenThrow(new RepositoryException(message));
        // exercise
        courseController.updateEntity(modifiedCourse);
        // verify
        verify(courseView)
                .showError("Repository exception: message, " + modifiedCourse);
    }

}
