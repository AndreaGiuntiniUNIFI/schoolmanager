package apt.project.frontend.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

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
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }

        dialogManager = new DialogManager();
        mainFrame = mock(MainFrame.class);

        GuiActionRunner.execute(() -> {
            internalExamPanel = new JPanel();
            examPanel = new ExamPanel(internalExamPanel, mainFrame,
                    dialogManager, HEADER_EXAM);
            internalStudentPanel = new JPanel();
            studentPanel = new StudentPanel(internalStudentPanel, examPanel,
                    mainFrame, dialogManager, HEADER_STUDENT);
            studentController = new StudentController(studentPanel,
                    studentRepository);
            studentPanel.setController(studentController);

            frame = new JFrame();
            cardsPanel = studentPanel.getCardsPanel();
            frame.add(cardsPanel);
            frame.pack();
            frame.setVisible(true);

        });
        panelFixture = new JPanelFixture(robot(), cardsPanel);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        entityManagerFactory.close();
    }

    @Test
    @GUITest
    public void testAllExams() throws RepositoryException {
        student = new Student("student");

        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 30);
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 28);

        student.addExam(exam1);
        student.addExam(exam2);
        try {
            courseRepository.save(course1);
            courseRepository.save(course2);
            studentRepository.save(student);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(student);
        });

        panelFixture.list().selectItem(0);
        panelFixture.button(JButtonMatcher.withText("Open")).click();

        assertThat(panelFixture.list().contents())
                .containsExactly(exam1.toString(), exam2.toString());
    }

}
