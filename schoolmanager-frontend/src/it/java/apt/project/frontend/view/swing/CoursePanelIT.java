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
import apt.project.backend.repository.CourseRepository;
import apt.project.backend.repository.RepositoryException;
import apt.project.backend.repository.TransactionManager;
import apt.project.frontend.controller.CourseController;
import apt.project.frontend.view.MainFrame;
import apt.project.frontend.view.swing.dialog.DialogManager;

@RunWith(GUITestRunner.class)
public class CoursePanelIT extends AssertJSwingJUnitTestCase {

    private static final String HEADER_TEXT = "List of Courses";

    private CoursePanel coursePanel;

    private JPanel internalPanel;
    private JPanelFixture panelFixture;
    private JFrame frame;
    private DialogManager dialogManager;
    private CourseController courseController;
    private MainFrame mainFrame;

    private static EntityManagerFactory entityManagerFactory;
    private CourseRepository courseRepository;
    private static TransactionManager<Course> transactionManager;

    @BeforeClass
    public static void setUpClass() throws Exception {
        entityManagerFactory = Persistence.createEntityManagerFactory("H2");
        transactionManager = new TransactionManager<>(entityManagerFactory);
    }

    @Override
    protected void onSetUp() {
        courseRepository = new CourseRepository(transactionManager);
        // explicit empty the database through the repository
        try {
            for (Course course : courseRepository.findAll()) {
                courseRepository.delete(course);
            }
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }

        dialogManager = new DialogManager();
        mainFrame = mock(MainFrame.class);

        GuiActionRunner.execute(() -> {
            internalPanel = new JPanel();
            coursePanel = new CoursePanel(internalPanel, mainFrame,
                    dialogManager, HEADER_TEXT);
            courseController = new CourseController(coursePanel,
                    courseRepository);
            coursePanel.setController(courseController);
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
        // use the repository to add students to the database
        Course course1 = new Course("course1");
        Course course2 = new Course("course2");
        courseRepository.save(course1);
        courseRepository.save(course2);
        // use the controller's allStudents
        GuiActionRunner.execute(() -> courseController.allEntities());
        // and verify that the view's list is populated
        assertThat(panelFixture.list().contents())
                .containsExactly(course1.toString(), course2.toString());
    }

    @Test
    @GUITest
    public void testAddButtonSuccess() {
        // setup
        // exercise
        panelFixture.button(JButtonMatcher.withText("Add")).click();
        String title = "title1";
        panelFixture.dialog().textBox("TitleTextField").enterText(title);
        panelFixture.dialog().button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(panelFixture.list().contents())
                .containsExactly(new Course("title1").toString());
    }

    @Test
    @GUITest
    public void testAddButtonError() throws RepositoryException {
        String existingTitle = "title";
        Course course = new Course(existingTitle);
        courseRepository.save(course);
        panelFixture.button(JButtonMatcher.withText("Add")).click();
        panelFixture.dialog().textBox().enterText(existingTitle);
        panelFixture.dialog().button(JButtonMatcher.withText("OK")).click();
        assertThat(panelFixture.list().contents()).isEmpty();

        verify(mainFrame).displayErrorLabel(
                "Already existing entity: " + course + ": " + course);
    }

}
