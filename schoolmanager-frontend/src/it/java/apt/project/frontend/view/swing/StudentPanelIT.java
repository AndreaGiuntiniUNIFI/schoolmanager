package apt.project.frontend.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import apt.project.backend.domain.Student;
import apt.project.backend.repository.RepositoryException;
import apt.project.backend.repository.StudentRepository;
import apt.project.backend.repository.TransactionManager;
import apt.project.frontend.controller.StudentController;
import apt.project.frontend.view.MainFrame;
import apt.project.frontend.view.swing.dialog.DialogManager;

@RunWith(GUITestRunner.class)
public class StudentPanelIT extends AssertJSwingJUnitTestCase {

    private static final String HEADER_TEXT = "List of Students";

    private StudentPanel studentPanel;

    private JPanel internalPanel;
    private JPanelFixture panelFixture;
    private JFrame frame;
    private DialogManager dialogManager;
    private StudentController studentController;
    private MainFrame mainFrame;

    private static EntityManagerFactory entityManagerFactory;
    private StudentRepository studentRepository;
    private static TransactionManager<Student> transactionManager;

    @BeforeClass
    public static void setUpClass() throws Exception {
        entityManagerFactory = Persistence.createEntityManagerFactory("H2");
        transactionManager = new TransactionManager<>(entityManagerFactory);
    }

    @Override
    protected void onSetUp() {
        studentRepository = new StudentRepository(transactionManager);
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
            ExamPanel examPanel = spy(new ExamPanel(new JPanel(), mainFrame,
                    dialogManager, "List of Exams"));
            internalPanel = new JPanel();
            studentPanel = new StudentPanel(internalPanel, examPanel, mainFrame,
                    dialogManager, HEADER_TEXT);
            studentController = new StudentController(studentPanel,
                    studentRepository);
            studentPanel.setController(studentController);
            frame = new JFrame();
            frame.add(internalPanel);
            frame.pack();
            frame.setVisible(true);
        });

        panelFixture = new JPanelFixture(robot(), internalPanel);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        entityManagerFactory.close();
    }

    @Test
    @GUITest
    public void testAllCourses() throws RepositoryException {
        // use the repository to add course to the database
        Student student1 = new Student("student1");
        Student student2 = new Student("student2");
        studentRepository.save(student1);
        studentRepository.save(student2);
        // use the controller's allEntities
        GuiActionRunner.execute(() -> studentController.allEntities());
        // and verify that the view's list is populated
        assertThat(panelFixture.list().contents())
                .containsExactly(student1.toString(), student2.toString());
    }

}
