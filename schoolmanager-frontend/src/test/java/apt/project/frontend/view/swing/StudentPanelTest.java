package apt.project.frontend.view.swing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import apt.project.backend.domain.Student;
import apt.project.frontend.controller.StudentController;

@RunWith(GUITestRunner.class)

public class StudentPanelTest extends AssertJSwingJUnitTestCase {

    private StudentPanel studentPanel;
    private JFrame jframe;
    private JPanelFixture panelFixture;
    private DialogManager dialogManager;
    private StudentController studentController;

    @Override
    protected void onSetUp() {

        GuiActionRunner.execute(() -> {
            dialogManager = mock(DialogManager.class);
            studentController = mock(StudentController.class);
            studentPanel = new StudentPanel(dialogManager);
            studentPanel.setStudentController(studentController);
            return studentPanel;
        });

        GuiActionRunner.execute(() -> {
            jframe = new JFrame();
            jframe.getContentPane().add(studentPanel);
            jframe.pack();
            jframe.setVisible(true);
            return jframe;
        });

        panelFixture = new JPanelFixture(robot(), studentPanel);

    }

    @Test
    @GUITest
    public void testControlsInitialStates() {

        panelFixture.label(JLabelMatcher.withText("List of Students"));
        panelFixture.button(JButtonMatcher.withText("Add")).requireEnabled();
        panelFixture.button(JButtonMatcher.withText("Delete"))
                .requireDisabled();
        panelFixture.button(JButtonMatcher.withText("Modify"))
                .requireDisabled();
        panelFixture.list("studentList");
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
    public void testWhenStudentIsSelectedDeleteIsEnabled() {
        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(new Student("name1"));
        });

        panelFixture.list("studentList").selectItem(0);

        JButtonFixture buttonDelete = panelFixture
                .button(JButtonMatcher.withText("Delete"));

        buttonDelete.requireEnabled();

        panelFixture.list("studentList").clearSelection();

        buttonDelete.requireDisabled();

    }

    @Test
    @GUITest
    public void testWhenStudentIsSelectedModifyIsEnabled() {
        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(new Student("name1"));
        });

        panelFixture.list("studentList").selectItem(0);

        JButtonFixture buttonModify = panelFixture
                .button(JButtonMatcher.withText("Modify"));

        buttonModify.requireEnabled();

        panelFixture.list("studentList").clearSelection();

        buttonModify.requireDisabled();

    }

}
