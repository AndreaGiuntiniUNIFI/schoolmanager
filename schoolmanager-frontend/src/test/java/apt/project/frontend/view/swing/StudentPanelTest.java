package apt.project.frontend.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import apt.project.backend.domain.Student;
import apt.project.frontend.controller.ExamController;
import apt.project.frontend.controller.StudentController;
import apt.project.frontend.view.MainFrame;
import apt.project.frontend.view.swing.dialog.DialogManager;

@RunWith(GUITestRunner.class)
public class StudentPanelTest extends AssertJSwingJUnitTestCase {

    private static final String HEADER_TEXT = "List of Students";

    private StudentPanel studentPanel;

    private JPanel internalStudentPanel;
    private JPanelFixture panelFixture;
    private JFrame frame;
    private DialogManager dialogManager;
    private StudentController studentController;
    private ExamController examController;
    private MainFrame mainFrame;
    private JPanel internalExamPanel;

    private JPanel cardsPanel;

    private ExamPanel examPanel;

    @Override
    protected void onSetUp() {

        dialogManager = mock(DialogManager.class);
        mainFrame = mock(MainFrame.class);
        studentController = mock(StudentController.class);
        examController = mock(ExamController.class);

        GuiActionRunner.execute(() -> {
            internalExamPanel = new JPanel();
            examPanel = spy(new ExamPanel(internalExamPanel, dialogManager,
                    "List of Exams"));
            examPanel.setMainFrame(mainFrame);

            internalStudentPanel = new JPanel();
            studentPanel = new StudentPanel(internalStudentPanel, examPanel,
                    dialogManager, examController, HEADER_TEXT);
            studentPanel.setMainFrame(mainFrame);
            studentPanel.setController(studentController);
            frame = new JFrame();
            cardsPanel = studentPanel.getCardsPanel();
            frame.add(cardsPanel);
            frame.pack();
            frame.setVisible(true);
        });

        panelFixture = new JPanelFixture(robot(), internalStudentPanel);
    }

    @Test
    @GUITest
    public void testControlsInitialStates() {
        panelFixture.button(JButtonMatcher.withText("Open")).requireDisabled();
    }

    @Test
    @GUITest
    public void testWhenAddButtonIsClickedDialogManagerIsCalled() {
        panelFixture.button(JButtonMatcher.withText("Add")).click();
        verify(dialogManager).manageDialog("Name");
    }

    @Test
    @GUITest
    public void testWhenAddButtonIsClickedAndDialogManagerReturnsOutcomeThenControllerIsCalled() {
        when(dialogManager.manageDialog("Name")).thenReturn("name1");

        panelFixture.button(JButtonMatcher.withText("Add")).click();
        verify(studentController).newEntity(new Student("name1"));
    }

    @Test
    @GUITest
    public void testWhenAddButtonIsClickedAndDialogManagerReturnsNullThenControllerIsNotCalled() {
        when(dialogManager.manageDialog("Name")).thenReturn(null);

        panelFixture.button(JButtonMatcher.withText("Add")).click();
        verifyZeroInteractions(studentController);
    }

    @Test
    @GUITest
    public void testWhenDeleteButtonIsClickedThenControllerIsCalled() {
        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(new Student("name1"));
        });

        panelFixture.list("entityList").selectItem(0);

        panelFixture.button(JButtonMatcher.withText("Delete")).click();
        verify(studentController).deleteEntity(new Student("name1"));

    }

    @Test
    @GUITest
    public void testWhenModifyButtonIsClickedDialogManagerIsCalled() {
        String name = "name1";
        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(new Student(name));
        });

        panelFixture.list("entityList").selectItem(0);

        panelFixture.button(JButtonMatcher.withText("Modify")).click();
        verify(dialogManager).manageDialog("Name", name);
    }

    @Test
    @GUITest
    public void testWhenModifyButtonIsClickedAndDialogManagerReturnsOutcomeThenControllerIsCalled() {
        String name = "name1";
        String modifiedName = "modifiedName";
        Student student = new Student(name);

        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(student);
        });

        panelFixture.list("entityList").selectItem(0);

        when(dialogManager.manageDialog("Name", name)).thenReturn(modifiedName);

        panelFixture.button(JButtonMatcher.withText("Modify")).click();

        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor
                .forClass(Student.class);

        verify(studentController).updateEntity(studentCaptor.capture());
        assertThat(studentCaptor.getValue().getName()).isEqualTo(modifiedName);
        assertThat(studentCaptor.getValue()).isEqualTo(student);

    }

    @Test
    @GUITest
    public void testWhenModifyButtonIsClickedAndDialogManagerReturnsNullThenControllerIsNotCalled() {
        String name = "name1";

        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(new Student(name));
        });

        panelFixture.list("entityList").selectItem(0);

        when(dialogManager.manageDialog("Name", name)).thenReturn(null);

        panelFixture.button(JButtonMatcher.withText("Modify")).click();
        verifyZeroInteractions(studentController);
    }

    @Test
    @GUITest
    public void testWhenEntityIsSelectedOpenIsEnabled() {
        // setup
        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(new Student("name"));
        });
        JButtonFixture buttonOpen = panelFixture
                .button(JButtonMatcher.withText("Open"));

        // exercise
        panelFixture.list("entityList").selectItem(0);
        // verify
        buttonOpen.requireEnabled();

        // exercise
        panelFixture.list("entityList").clearSelection();
        // verify
        buttonOpen.requireDisabled();

    }

    @Test
    @GUITest
    public void testWhenOpenButtonIsClickedExamPanelIsShown() {
        // setup
        Student student1 = new Student("name1");
        Student student2 = new Student("name2");
        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(student1);
            studentPanel.getListModel().addElement(student2);
        });
        panelFixture.list("entityList").selectItem(1);
        JPanelFixture cardsFixture = new JPanelFixture(robot(), cardsPanel);

        // exercise
        panelFixture.button(JButtonMatcher.withText("Open")).click();

        // verify
        InOrder inOrder = Mockito.inOrder(examPanel, examController);
        inOrder.verify(examPanel).setStudent(student2);
        inOrder.verify(examController).allEntities(student2);
        cardsFixture.panel("examPanel").requireVisible();
        panelFixture.requireNotVisible();
    }

    @Test
    @GUITest
    public void testBackButtonInExamPanelSwitchesBackToStudentPanel() {
        // setup
        String name = "name1";
        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(new Student(name));
        });
        panelFixture.list("entityList").selectItem(0);
        panelFixture.button(JButtonMatcher.withText("Open")).click();
        JPanelFixture cardsFixture = new JPanelFixture(robot(), cardsPanel);
        JPanelFixture examPanelFixture = cardsFixture.panel("examPanel");

        // exercise
        examPanelFixture.button(JButtonMatcher.withText("Back")).click();

        // verify
        verify(examPanel).clearListModel();
        panelFixture.requireVisible();
        examPanelFixture.requireNotVisible();
    }

}
