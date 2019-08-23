package apt.project.frontend.controller;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Exam;
import apt.project.backend.domain.Student;
import apt.project.backend.repository.RepositoryException;
import apt.project.backend.repository.StudentRepository;
import apt.project.frontend.view.View;

public class ExamControllerTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private View<Exam> examView;

    @InjectMocks
    private ExamController examController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAllEntitiesShouldCallView() throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 22);
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 21);
        Student student = new Student("student");
        student.addExam(exam1);
        student.addExam(exam2);
        // exercise
        examController.allEntities(student);
        // verify
        verify(examView).showAll(asList(exam1, exam2));
    }

    @Test
    public void testAllEntitiesWhenStudentIsNullShouldShowError()
            throws RepositoryException {
        // exercise
        examController.allEntities(null);
        // verify
        verify(examView).showError("No existing entity", null);
        verifyNoMoreInteractions(studentRepository, examView);

    }

    @Test
    public void testNewExam() throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 22);
        String name = "student";
        Student student = new Student(name);
        student.addExam(exam1);

        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 22);

        // exercise
        examController.newEntity(student, exam2);
        // verify
        InOrder inOrder = inOrder(studentRepository, examView);
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor
                .forClass(Student.class);

        inOrder.verify(studentRepository).update(studentCaptor.capture());
        assertThat(studentCaptor.getValue().getName()).isEqualTo(name);
        assertThat(studentCaptor.getValue().getExams()).containsExactly(exam1,
                exam2);

        inOrder.verify(examView).entityAdded(exam2);
    }

    @Test
    public void testNewEntityWhenRepositoryExceptionIsThrownInSaveShouldCallShowError()
            throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 22);
        String name = "student";
        Student student = new Student(name);
        student.addExam(exam1);

        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 22);

        String message = "message";
        doThrow(new RepositoryException(message)).when(studentRepository)
                .update(any(Student.class));
        // exercise
        examController.newEntity(student, exam2);
        // verify
        verify(examView).showError("Repository exception: " + message, exam2);
        verifyNoMoreInteractions(examView);
    }

}
