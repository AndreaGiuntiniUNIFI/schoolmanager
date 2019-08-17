package apt.project.frontend.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
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

import apt.project.backend.domain.Student;
import apt.project.frontend.controller.StudentController;
import apt.project.frontend.view.MainFrame;

@RunWith(GUITestRunner.class)
public class StudentPanelTest extends AssertJSwingJUnitTestCase {

    private static final String HEADER_TEXT = "List of Students";

    private StudentPanel studentPanel;

    private JPanel internalPanel;
    private JPanelFixture panelFixture;
    private JFrame frame;
    private DialogManager dialogManager;
    private StudentController studentController;
    private MainFrame mainFrame;
    private JPanel examPanel;

    private JPanel cardsPanel;

    @Override
    protected void onSetUp() {

        dialogManager = mock(DialogManager.class);
        mainFrame = mock(MainFrame.class);
        studentController = mock(StudentController.class);

        GuiActionRunner.execute(() -> {
            internalPanel = new JPanel();
            examPanel = new JPanel();
            studentPanel = new StudentPanel(internalPanel, examPanel, mainFrame,
                    dialogManager, HEADER_TEXT);
            studentPanel.setController(studentController);
            frame = new JFrame();
            cardsPanel = studentPanel.getCardsPanel();
            frame.add(cardsPanel);
            frame.pack();
            frame.setVisible(true);
        });

        panelFixture = new JPanelFixture(robot(), internalPanel);
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
        String name = "name1";
        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(new Student(name));
        });
        panelFixture.list("entityList").selectItem(0);
        JPanelFixture cardsFixture = new JPanelFixture(robot(), cardsPanel);

        // exercise
        panelFixture.button(JButtonMatcher.withText("Open")).click();

        // verify
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
        panelFixture.requireVisible();
        examPanelFixture.requireNotVisible();
    }

}
