package apt.project.frontend.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
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

import apt.project.backend.domain.Student;
import apt.project.backend.repository.RepositoryException;
import apt.project.backend.repository.StudentRepository;
import apt.project.backend.repository.TransactionManager;
import apt.project.frontend.controller.ExamController;
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

    private ExamController examController;

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
        examController = mock(ExamController.class);

        GuiActionRunner.execute(() -> {
            ExamPanel examPanel = spy(new ExamPanel(new JPanel(), dialogManager,
                    "List of Exams"));
            examPanel.setMainFrame(mainFrame);
            internalPanel = new JPanel();
            studentPanel = new StudentPanel(internalPanel, examPanel,
                    dialogManager, examController, HEADER_TEXT);
            studentPanel.setMainFrame(mainFrame);
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
    public void testAllStudents() throws RepositoryException {
        // use the repository to add student to the database
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

    @Test
    @GUITest
    public void testAddButtonSuccess() {
        // setup
        // exercise
        panelFixture.button(JButtonMatcher.withText("Add")).click();
        String name = "name1";
        panelFixture.dialog().textBox("NameTextField").enterText(name);
        panelFixture.dialog().button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(panelFixture.list().contents())
                .containsExactly(new Student("name1").toString());
    }

    @Test
    @GUITest
    public void testAddButtonError() throws RepositoryException {
        String existingName = "name";
        Student student = new Student(existingName);
        studentRepository.save(student);
        panelFixture.button(JButtonMatcher.withText("Add")).click();
        panelFixture.dialog().textBox("NameTextField").enterText(existingName);
        panelFixture.dialog().button(JButtonMatcher.withText("OK")).click();
        assertThat(panelFixture.list().contents()).isEmpty();

        verify(mainFrame)
                .displayErrorLabel("Already existing entity: " + student);
    }

    @Test
    @GUITest
    public void testDeleteButtonSuccess() {
        // use the controller to populate the view's list...
        GuiActionRunner.execute(() -> studentController
                .newEntity(new Student("studentToRemove")));
        // ...with a course to select
        panelFixture.list().selectItem(0);
        panelFixture.button(JButtonMatcher.withText("Delete")).click();
        assertThat(panelFixture.list().contents()).isEmpty();
    }

    @Test
    @GUITest
    public void testDeleteButtonError() {
        // manually add a course to the list, which will not be in the db
        Student student = new Student("non existent");
        student.setId(1L);
        GuiActionRunner
                .execute(() -> studentPanel.getListModel().addElement(student));
        panelFixture.list().selectItem(0);
        panelFixture.button(JButtonMatcher.withText("Delete")).click();
        assertThat(panelFixture.list().contents())
                .containsExactly(student.toString());

        verify(mainFrame).displayErrorLabel("No existing entity: " + student);
    }

    @Test
    @GUITest
    public void testUpdateButtonSuccess() {
        // use the controller to populate the view's list...
        GuiActionRunner.execute(() -> studentController
                .newEntity(new Student("studentToModify")));
        // ...with a student to select
        panelFixture.list().selectItem(0);
        // exercise
        panelFixture.button(JButtonMatcher.withText("Modify")).click();
        String name = "modifiedName";
        panelFixture.dialog().textBox("NameTextField").deleteText()
                .enterText(name);
        panelFixture.dialog().button(JButtonMatcher.withText("OK")).click();
        // verify
        assertThat(panelFixture.list().contents())
                .containsExactly(new Student(name).toString());
    }

    @Test
    @GUITest
    public void testUpdateButtonError() throws RepositoryException {
        String existingName = "name";
        Student student = new Student(existingName);
        studentRepository.save(student);
        // use the controller to populate the view's list...
        GuiActionRunner.execute(
                () -> studentController.newEntity(new Student("nameToModify")));
        // ...with a student to select
        panelFixture.list().selectItem(0);
        // exercise
        panelFixture.button(JButtonMatcher.withText("Modify")).click();
        panelFixture.dialog().textBox("NameTextField").deleteText()
                .enterText(existingName);
        panelFixture.dialog().button(JButtonMatcher.withText("OK")).click();
        // verify

        verify(mainFrame).displayErrorLabel(
                "Already existing student with this name: " + student);
    }

}
