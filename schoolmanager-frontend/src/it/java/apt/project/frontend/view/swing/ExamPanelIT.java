package apt.project.frontend.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Exam;
import apt.project.backend.domain.Student;
import apt.project.backend.repository.CourseRepository;
import apt.project.backend.repository.RepositoryException;
import apt.project.backend.repository.StudentRepository;
import apt.project.backend.repository.TransactionManager;
import apt.project.frontend.controller.ExamController;
import apt.project.frontend.controller.ExamDialogController;
import apt.project.frontend.controller.StudentController;
import apt.project.frontend.view.MainFrame;
import apt.project.frontend.view.swing.dialog.DialogManager;

@RunWith(GUITestRunner.class)
public class ExamPanelIT extends AssertJSwingJUnitTestCase {

    private static final String HEADER_EXAM = "List of Exams";

    private static final String HEADER_STUDENT = "List of Students";

    private StudentPanel studentPanel;

    private JPanel internalStudentPanel;
    private JPanelFixture panelFixture;
    private JFrame frame;
    private DialogManager dialogManager;
    private StudentController studentController;
    private CourseRepository courseRepository;
    private MainFrame mainFrame;

    private static EntityManagerFactory entityManagerFactory;
    private StudentRepository studentRepository;
    private static TransactionManager<Student> transactionManager;
    private static TransactionManager<Course> transactionManagerCourse;

    private ExamPanel examPanel;

    private JPanel internalExamPanel;

    private JPanel cardsPanel;

    private Student student;

    private Course course1;

    private Exam exam1;

    private ExamDialogController examDialogController;

    private ExamController examController;

    @BeforeClass
    public static void setUpClass() throws Exception {
        entityManagerFactory = Persistence.createEntityManagerFactory("H2");
        transactionManager = new TransactionManager<>(entityManagerFactory);
        transactionManagerCourse = new TransactionManager<>(
                entityManagerFactory);
    }

    @Override
    protected void onSetUp() {
        studentRepository = new StudentRepository(transactionManager);
        courseRepository = new CourseRepository(transactionManagerCourse);
        // explicit empty the database through the repository

        try {

            for (Student student : studentRepository.findAll()) {
                studentRepository.delete(student);
            }

            for (Course course : courseRepository.findAll()) {
                courseRepository.delete(course);
            }

        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        dialogManager = new DialogManager();
        mainFrame = mock(MainFrame.class);

        GuiActionRunner.execute(() -> {
            internalExamPanel = new JPanel();
            examPanel = new ExamPanel(internalExamPanel, dialogManager,
                    HEADER_EXAM);
            examPanel.setMainFrame(mainFrame);
            examController = new ExamController(examPanel, studentRepository);
            internalStudentPanel = new JPanel();
            studentPanel = new StudentPanel(internalStudentPanel, examPanel,
                    dialogManager, examController, HEADER_STUDENT);
            studentPanel.setMainFrame(mainFrame);
            studentController = new StudentController(studentPanel,
                    studentRepository);
            examPanel.setController(examController);
            examDialogController = new ExamDialogController(examPanel,
                    courseRepository);
            examPanel.setExamDialogController(examDialogController);

            studentPanel.setController(studentController);

            frame = new JFrame();
            cardsPanel = studentPanel.getCardsPanel();
            frame.add(cardsPanel);
            frame.pack();
            frame.setVisible(true);

        });
        panelFixture = new JPanelFixture(robot(), cardsPanel);

        // Initialization for Exam panel related to "student" entity
        student = new Student("student");

        course1 = new Course("course1");
        exam1 = new Exam(course1, 30);

        student.addExam(exam1);
        try {
            courseRepository.save(course1);
            studentRepository.save(student);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(student);
        });

        panelFixture.list().selectItem(0);
        panelFixture.button(JButtonMatcher.withText("Open")).click();

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        entityManagerFactory.close();
    }

    @Test
    @GUITest
    public void testAllExams() throws RepositoryException {
        assertThat(panelFixture.list().contents())
                .containsExactly(exam1.toString());
    }

    @Test
    @GUITest
    public void testAddButtonSuccess() throws RepositoryException {
        // setup
        Course course2 = new Course("course3");
        courseRepository.save(course2);

        // exercise
        panelFixture.button(JButtonMatcher.withText("Add").andShowing())
                .click();

        panelFixture.dialog().comboBox("examComboBox").selectItem(0);
        panelFixture.dialog().comboBox("rateComboBox").selectItem(1);

        panelFixture.dialog().button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(panelFixture.list().contents()).containsExactlyInAnyOrder(
                exam1.toString(), new Exam(course2, 19).toString());
    }

    @Test
    @GUITest
    public void testDeleteButtonSuccess() {
        // ...with a course to select
        panelFixture.list().selectItem(0);
        panelFixture.button(JButtonMatcher.withText("Delete").andShowing())
                .click();
        assertThat(panelFixture.list().contents()).isEmpty();
    }

    @Test
    @GUITest
    public void testDeleteButtonError() {
        // manually add a course to the list, which will not be in the db
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 30);

        GuiActionRunner
                .execute(() -> examPanel.getListModel().addElement(exam2));

        panelFixture.list().selectItem(1);
        panelFixture.button(JButtonMatcher.withText("Delete").andShowing())
                .click();
        assertThat(panelFixture.list().contents())
                .containsExactly(exam1.toString(), exam2.toString());

        verify(mainFrame)
                .displayErrorLabel("Exam not registered in student: " + exam2);
    }

    @Test
    @GUITest
    public void testUpdateButtonSuccess() {
        // select an exam
        panelFixture.list().selectItem(0);
        // exercise
        panelFixture.button(JButtonMatcher.withText("Modify").andShowing())
                .click();
        panelFixture.dialog().comboBox("rateComboBox").selectItem(1);

        panelFixture.dialog().button(JButtonMatcher.withText("OK")).click();
        // verify
        assertThat(panelFixture.list().contents())
                .containsExactly(new Exam(course1, 19).toString());
    }

}
