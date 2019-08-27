package apt.project.frontend.controller;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
    public void testAllEntitiesShouldCallRepositoryAndView()
            throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 22);
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 21);
        Student student = new Student("student");
        student.setId(1L);

        when(studentRepository.getAllExams(1L))
                .thenReturn(asList(exam1, exam2));

        // exercise
        examController.allEntities(student);

        // verify
        InOrder inOrder = inOrder(examView, studentRepository);
        inOrder.verify(studentRepository).getAllExams(1L);
        inOrder.verify(examView).showAll(asList(exam1, exam2));
        assertThat(student.getExams()).containsExactly(exam1, exam2);
    }

    @Test
    public void testAllEntitiesWhenRepositoryExceptionIsThrownShouldCallShowError()
            throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 22);
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 21);
        Student student = new Student("student");
        student.setId(1L);
        student.addExam(exam1);
        student.addExam(exam2);
        String message = "message";
        when(studentRepository.getAllExams(1L))
                .thenThrow(new RepositoryException(message));

        // exercise
        examController.allEntities(student);

        // verify
        verify(examView).showError("Repository exception: " + message, null);
        verifyNoMoreInteractions(examView);
        verifyNoMoreInteractions(ignoreStubs(studentRepository));
    }

    @Test
    public void testAllEntitiesWhenStudentIsNullShouldShowError()
            throws RepositoryException {
        // exercise
        examController.allEntities(null);

        // verify
        verify(examView).showError("Student is null", null);
        verifyNoMoreInteractions(studentRepository, examView);

    }

    @Test
    public void testNewEntity() throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 22);
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 22);

        String name = "student";
        Student student = new Student(name);
        student.addExam(exam1);

        // exercise
        examController.newEntity(student, exam2);

        // verify
        InOrder inOrder = inOrder(studentRepository, examView);
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor
                .forClass(Student.class);

        inOrder.verify(studentRepository).update(studentCaptor.capture());
        inOrder.verify(examView).entityAdded(exam2);

        assertThat(studentCaptor.getValue().getName()).isEqualTo(name);
        assertThat(studentCaptor.getValue().getExams()).containsExactly(exam1,
                exam2);
    }

    @Test
    public void testNewEntityIfExamIsAlreadyRegisteredShouldCallShowError()
            throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam exam = new Exam(course1, 22);
        Exam duplicatedExam = new Exam(course1, 29);

        String name = "student";
        Student student = new Student(name);
        student.addExam(exam);

        // exercise
        examController.newEntity(student, duplicatedExam);

        // verify
        verify(examView).showError("Exam already registered in student",
                duplicatedExam);
        verifyNoMoreInteractions(examView);
    }

    @Test
    public void testNewEntityWhenRepositoryExceptionIsThrownInUpdateShouldCallShowError()
            throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 22);
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 22);

        Student student = new Student("student");
        student.addExam(exam1);

        String message = "message";
        doThrow(new RepositoryException(message)).when(studentRepository)
                .update(student);

        // exercise
        examController.newEntity(student, exam2);
        // verify
        verify(examView).showError("Repository exception: " + message, exam2);
        assertThat(student.getExams()).containsExactly(exam1);
        verifyNoMoreInteractions(examView);
    }

    @Test
    public void testDeleteEntity() throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 22);
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 22);

        Student student = new Student("student");
        student.addExam(exam1);
        student.addExam(exam2);

        // exercise
        examController.deleteEntity(student, exam1);

        // verify
        InOrder inOrder = inOrder(studentRepository, examView);
        inOrder.verify(studentRepository).update(student);
        inOrder.verify(examView).entityDeleted(exam1);
    }

    @Test
    public void testDeleteEntityIfExamIsNotRegisteredShouldCallShowError()
            throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 22);
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 22);

        Student student = new Student("student");
        student.addExam(exam1);

        // exercise
        examController.deleteEntity(student, exam2);

        // verify
        verify(examView).showError("Exam not registered in student", exam2);
        verifyNoMoreInteractions(examView);
    }

    @Test
    public void testDeleteEntityWhenRepositoryExceptionIsThrownInUpdateShouldCallShowError()
            throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 22);

        Student student = new Student("student");
        student.addExam(exam1);
        String message = "message";
        doThrow(new RepositoryException(message)).when(studentRepository)
                .update(student);

        // exercise
        examController.deleteEntity(student, exam1);

        // verify
        verify(examView).showError("Repository exception: " + message, exam1);
        assertThat(student.getExams()).containsExactly(exam1);
        verifyNoMoreInteractions(examView);
    }

    @Test
    public void testUpdateEntity() throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam originalExam = new Exam(course1, 22);
        Exam modifiedExam = new Exam(course1, 28);

        Student student = new Student("student");
        student.addExam(originalExam);

        // exercise
        examController.updateEntity(student, modifiedExam);

        // verify
        InOrder inOrder = inOrder(studentRepository, examView);
        inOrder.verify(studentRepository).update(student);
        inOrder.verify(examView).entityUpdated(modifiedExam);
        assertThat(student.getExams()).containsExactly(modifiedExam);
    }

    @Test
    public void testUpdateEntityWhenExamIsNotRegisteredInStudent()
            throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 22);
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 22);

        Student student = new Student("student");
        student.addExam(exam1);

        // exercise
        examController.updateEntity(student, exam2);

        // verify
        verify(examView).showError("Exam not registered in student", exam2);
        verifyNoMoreInteractions(examView);
    }

    @Test
    public void testUpdateEntityWhenRepositoryExceptionIsThrownInUpdateShouldCallShowError()
            throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam originalExam = new Exam(course1, 22);
        Exam modifiedExam = new Exam(course1, 28);

        Student student = new Student("student");
        student.addExam(originalExam);
        String message = "message";
        doThrow(new RepositoryException(message)).when(studentRepository)
                .update(student);

        // exercise
        examController.updateEntity(student, modifiedExam);

        // verify
        verify(examView).showError("Repository exception: " + message,
                modifiedExam);
        assertThat(student.getExams()).containsExactly(originalExam);
        verifyNoMoreInteractions(examView);
    }

}
