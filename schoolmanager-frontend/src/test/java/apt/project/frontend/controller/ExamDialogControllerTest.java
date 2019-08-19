package apt.project.frontend.controller;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Exam;
import apt.project.backend.repository.CourseRepository;
import apt.project.backend.repository.RepositoryException;
import apt.project.frontend.view.View;
import apt.project.frontend.view.swing.ExamDialog;

public class ExamDialogControllerTest {

    @Mock
    private View<Exam> view;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ExamDialog dialog;

    @InjectMocks
    private ExamDialogController controller;

    @Captor
    private ArgumentCaptor<List<Course>> listCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPopulateComboBoxShouldCallDialogAndReturnTrue()
            throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Course course2 = new Course("course2");
        Course course3 = new Course("course3");
        Course course4 = new Course("course4");

        List<Course> courses = asList(course1, course3);
        List<Course> coursesInRepository = asList(course1, course2, course3,
                course4);
        List<Course> difference = asList(course4, course2);

        when(courseRepository.findAll()).thenReturn(coursesInRepository);

        // exercise
        boolean result = controller.populateComboBox(courses);

        // verify
        verify(dialog).setCoursesComboBox(listCaptor.capture());
        assertThat(listCaptor.getValue()).containsAll(difference);
        assertThat(result).isTrue();
    }

    @Test
    public void testPopulateComboBoxWhenRepositoryExceptionIsThrownInFindAllShouldCallShowErrorAndReturnFalse()
            throws RepositoryException {
        // setup
        String message = "message";
        Course course1 = new Course("course1");
        Course course2 = new Course("course2");

        List<Course> courses = asList(course1, course2);

        when(courseRepository.findAll())
                .thenThrow(new RepositoryException(message));

        // exercise
        boolean result = controller.populateComboBox(courses);

        // verify
        verify(view).showError("Repository exception: " + message, null);
        assertThat(result).isFalse();
        verifyNoMoreInteractions(dialog);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testPopulateComboBoxWhenRepositoryReturnsEmptyListShouldCallShowErrorAndReturnFalse()
            throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Course course2 = new Course("course2");

        List<Course> courses = asList(course1, course2);

        when(courseRepository.findAll()).thenReturn(emptyList());

        // exercise
        boolean result = controller.populateComboBox(courses);

        // verify
        verify(view).showError("Cannot add an exam. No course existing.", null);
        assertThat(result).isFalse();
        verifyNoMoreInteractions(dialog);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testPopulateComboBoxWhenAllCoursesAreAlreadyRegisteredAsExamsShouldCallShowErrorAndReturnFalse()
            throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Course course2 = new Course("course2");

        List<Course> courses = asList(course2, course1);
        List<Course> coursesInRepository = asList(course1, course2);

        when(courseRepository.findAll()).thenReturn(coursesInRepository);

        // exercise
        boolean result = controller.populateComboBox(courses);

        // verify
        verify(view).showError(
                "Cannot add an exam. Alredy registered all exams.", null);
        assertThat(result).isFalse();
        verifyNoMoreInteractions(dialog);
        verifyNoMoreInteractions(view);
    }

}
