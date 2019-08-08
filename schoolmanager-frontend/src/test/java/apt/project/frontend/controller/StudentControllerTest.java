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

import apt.project.backend.domain.Student;
import apt.project.backend.repository.RepositoryException;
import apt.project.backend.repository.StudentRepository;
import apt.project.frontend.view.View;

public class StudentControllerTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private View<Student> studentView;

    @InjectMocks
    private StudentController studentController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAllEntitiesShouldCallView() throws RepositoryException {
        // setup
        List<Student> students = asList(new Student("name"));
        when(studentRepository.findAll()).thenReturn(students);
        // exercise
        studentController.allEntities();
        // verify
        verify(studentView).showAll(students);
    }

    @Test
    public void testAllEntitiesWhenRepositoryExceptionIsThrownInFindAllShouldShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        when(studentRepository.findAll())
                .thenThrow(new RepositoryException(message));
        // exercise
        studentController.allEntities();
        // verify
        verify(studentView).showError("Repository exception: " + message, null);
    }

}
