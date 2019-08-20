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
import apt.project.backend.domain.Exam;
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
        List<Student> students = asList(new Student("John"));
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

    @Test
    public void testNewEntityWhenEntityDoesNotAlreadyExist()
            throws RepositoryException {
        // setup
        Student student = new Student("Jhon");
        when(studentRepository.findByName("John")).thenReturn(null);
        // exercise
        studentController.newEntity(student);
        // verify
        InOrder inOrder = inOrder(studentRepository, studentView);
        inOrder.verify(studentRepository).save(student);
        inOrder.verify(studentView).entityAdded(student);
    }

    @Test
    public void testNewEntityWhenEntityAlreadyExists()
            throws RepositoryException {
        // setup
        Student student = new Student("Jhon");
        when(studentRepository.findByName("Jhon")).thenReturn(student);
        // exercise
        studentController.newEntity(student);
        // verify
        verify(studentView).showError("Already existing entity: " + student,
                student);
        verifyNoMoreInteractions(ignoreStubs(studentRepository));
    }

    @Test
    public void testNewEntityWhenRepositoryExceptionIsThrownInFindByNameShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        Student student = new Student("Jhon");
        when(studentRepository.findByName("Jhon"))
                .thenThrow(new RepositoryException(message));
        // exercise
        studentController.newEntity(student);
        // verify
        verify(studentView).showError("Repository exception: " + message,
                student);
    }

    @Test
    public void testNewEntityWhenRepositoryExceptionIsThrownInSaveShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        Student student = new Student("John");
        doThrow(new RepositoryException(message)).when(studentRepository)
                .save(student);
        // exercise
        studentController.newEntity(student);
        // verify
        verify(studentView).showError("Repository exception: " + message,
                student);
    }

    @Test
    public void testDeleteEntityWhenEntityExists() throws RepositoryException {
        // setup
        Student studentToDelete = new Student("John");
        when(studentRepository.findById((Long) any()))
                .thenReturn(studentToDelete);
        // exercise
        studentController.deleteEntity(studentToDelete);
        // verify
        InOrder inOrder = inOrder(studentRepository, studentView);
        inOrder.verify(studentRepository).delete(studentToDelete);
        inOrder.verify(studentView).entityDeleted(studentToDelete);
    }

    @Test
    public void testDeleteEntityWhenEntityDoesNotExist()
            throws RepositoryException {
        // setup
        Student studentToDelete = new Student("John");
        when(studentRepository.findById((Long) any())).thenReturn(null);
        // exercise
        studentController.deleteEntity(studentToDelete);
        // verify
        verify(studentView).showError("No existing entity: " + studentToDelete,
                studentToDelete);
    }

    @Test
    public void testDeleteEntityWhenRepositoryExceptionIsThrownInFindByIdShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        Student studentToDelete = new Student("John");
        when(studentRepository.findById((Long) any()))
                .thenThrow(new RepositoryException(message));
        // exercise
        studentController.deleteEntity(studentToDelete);
        // verify
        verify(studentView).showError("Repository exception: " + message,
                studentToDelete);
        verifyNoMoreInteractions(ignoreStubs(studentRepository));
    }

    @Test
    public void testDeleteEntityWhenRepositoryExceptionIsThrownInDeleteShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        Student studentToDelete = new Student("John");
        when(studentRepository.findById((Long) any()))
                .thenReturn(studentToDelete);
        doThrow(new RepositoryException(message)).when(studentRepository)
                .delete(studentToDelete);
        // exercise
        studentController.deleteEntity(studentToDelete);
        // verify
        verify(studentView).showError("Repository exception: " + message,
                studentToDelete);
        verifyNoMoreInteractions(ignoreStubs(studentRepository));
    }

    @Test
    public void testUpdateEntityWhenEntityExists() throws RepositoryException {
        // setup
        String studentName = "Jane";
        Student existingStudent = new Student("John");
        existingStudent.setId(1L);
        Student modifiedStudent = new Student(studentName);
        modifiedStudent.setId(2L);
        when(studentRepository.findByName(studentName)).thenReturn(null);
        when(studentRepository.findById(2L)).thenReturn(existingStudent);
        // exercise
        studentController.updateEntity(modifiedStudent);
        // verify
        InOrder inOrder = inOrder(studentRepository, studentView);
        inOrder.verify(studentRepository).update(modifiedStudent);
        inOrder.verify(studentView).entityUpdated(modifiedStudent);
    }

    @Test
    public void testUpdateEntityWhenEntityDoesNotExist()
            throws RepositoryException {
        // setup
        String modifiedName = "Jane";
        Student modifiedStudent = new Student(modifiedName);
        when(studentRepository.findByName(modifiedName)).thenReturn(null);
        when(studentRepository.findById((Long) any())).thenReturn(null);
        // exercise
        studentController.updateEntity(modifiedStudent);
        // verify
        verify(studentView).showError("No existing entity: " + modifiedStudent,
                modifiedStudent);
        verifyNoMoreInteractions(ignoreStubs(studentRepository));
    }

    @Test
    public void testUpdateEntityWhenRepositoryExceptionIsThrownInFindByIdShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        String modifiedName = "Jane";
        Student modifiedStudent = new Student(modifiedName);
        when(studentRepository.findByName(modifiedName)).thenReturn(null);
        when(studentRepository.findById((Long) any()))
                .thenThrow(new RepositoryException(message));
        // exercise
        studentController.updateEntity(modifiedStudent);
        // verify
        verify(studentView).showError("Repository exception: " + message,
                modifiedStudent);
        verifyNoMoreInteractions(ignoreStubs(studentRepository));
    }

    @Test
    public void testUpdateEntityWhenRepositoryExceptionIsThrownInUpdateShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        String modifiedName = "Jane";
        Student existingStudent = new Student("John");
        Student modifiedStudent = new Student(modifiedName);
        when(studentRepository.findByName(modifiedName)).thenReturn(null);
        when(studentRepository.findById((Long) any()))
                .thenReturn(existingStudent);
        doThrow(new RepositoryException(message)).when(studentRepository)
                .update(modifiedStudent);
        // exercise
        studentController.updateEntity(modifiedStudent);
        // verify
        verify(studentView).showError("Repository exception: " + message,
                modifiedStudent);
        verifyNoMoreInteractions(ignoreStubs(studentRepository));
    }

    @Test
    public void testUpdateEntityWhenModifiedTitleAlreadyExist()
            throws RepositoryException {
        // setup

        String modifiedName = "Jane";
        Student modifiedStudent = new Student(modifiedName);
        modifiedStudent.setId(1L);
        Student existingStudent = new Student(modifiedName);
        existingStudent.setId(2L);

        when(studentRepository.findByName(modifiedName))
                .thenReturn(existingStudent);
        // exercise
        studentController.updateEntity(modifiedStudent);
        // verify
        verify(studentView).showError(
                "Already existing entity: " + modifiedStudent, modifiedStudent);
        verifyNoMoreInteractions(ignoreStubs(studentRepository));
    }

    @Test
    public void testUpdateEntityWhenRepositoryExceptionIsThrownInFindByTitleShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        String modifiedTitle = "Jane";
        Student modifiedCourse = new Student(modifiedTitle);

        when(studentRepository.findByName(modifiedTitle))
                .thenThrow(new RepositoryException(message));
        // exercise
        studentController.updateEntity(modifiedCourse);
        // verify
        verify(studentView).showError("Repository exception: " + message,
                modifiedCourse);
    }

    @Test
    public void testUpdateEntityWithDuplicatedExam()
            throws RepositoryException {
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1);
        exam1.setRate(18);
        String studentName = "John";
        Student existingStudent = new Student(studentName);
        existingStudent.getExams().add(exam1);

        Student modifiedStudent = new Student(studentName);
        Exam exam2 = new Exam(course1);
        exam1.setRate(28);
        modifiedStudent.getExams().add(exam1);
        modifiedStudent.getExams().add(exam2);

        // exercise
        studentController.updateEntity(modifiedStudent);
        // verify
        verify(studentView).showError(
                "Duplicate Exams in Student: " + modifiedStudent,
                modifiedStudent);
        verifyNoMoreInteractions(ignoreStubs(studentRepository));
    }

    @Test
    public void testUpdateEntityWithoutDuplicatedExamAndNotModifiedName()
            throws RepositoryException {
        Course course1 = new Course("course1");
        Course course2 = new Course("course2");
        Exam exam1 = new Exam(course1);
        exam1.setRate(18);
        String studentName = "John";
        Student existingStudent = new Student(studentName);
        existingStudent.setId(1L);
        existingStudent.getExams().add(exam1);

        Student modifiedStudent = new Student(studentName);
        modifiedStudent.setId(1L);
        Exam exam2 = new Exam(course2);
        exam1.setRate(28);
        modifiedStudent.getExams().add(exam1);
        modifiedStudent.getExams().add(exam2);

        when(studentRepository.findById(1L)).thenReturn(existingStudent);

        when(studentRepository.findByName(studentName))
                .thenReturn(existingStudent);

        // exercise
        studentController.updateEntity(modifiedStudent);
        // verify
        InOrder inOrder = inOrder(studentRepository, studentView);
        inOrder.verify(studentRepository).update(modifiedStudent);
        inOrder.verify(studentView).entityUpdated(modifiedStudent);
    }

}
