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
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import apt.project.backend.domain.Course;
import apt.project.frontend.controller.CourseController;
import apt.project.frontend.view.MainFrame;
import apt.project.frontend.view.swing.dialog.DialogManager;

@RunWith(GUITestRunner.class)
public class CoursePanelTest extends AssertJSwingJUnitTestCase {

    private static final String HEADER_TEXT = "List of Courses";

    private CoursePanel coursePanel;

    private JPanel internalPanel;
    private JPanelFixture panelFixture;
    private JFrame frame;
    private DialogManager dialogManager;
    private CourseController courseController;
    private MainFrame mainFrame;

    @Override
    protected void onSetUp() {

        dialogManager = mock(DialogManager.class);
        mainFrame = mock(MainFrame.class);
        courseController = mock(CourseController.class);

        GuiActionRunner.execute(() -> {
            internalPanel = new JPanel();
            coursePanel = new CoursePanel(internalPanel, dialogManager,
                    HEADER_TEXT);
            coursePanel.setController(courseController);
            coursePanel.setMainFrame(mainFrame);
            frame = new JFrame();
            frame.add(internalPanel);
            frame.pack();
            frame.setVisible(true);
        });

        panelFixture = new JPanelFixture(robot(), internalPanel);

    }

    @Test
    @GUITest
    public void testWhenAddButtonIsClickedDialogManagerIsCalled() {
        panelFixture.button(JButtonMatcher.withText("Add")).click();
        verify(dialogManager).manageDialog("Title");
    }

    @Test
    @GUITest
    public void testWhenAddButtonIsClickedAndDialogManagerReturnsOutcomeThenControllerIsCalled() {
        when(dialogManager.manageDialog("Title")).thenReturn("title1");

        panelFixture.button(JButtonMatcher.withText("Add")).click();
        verify(courseController).newEntity(new Course("title1"));
    }

    @Test
    @GUITest
    public void testWhenAddButtonIsClickedAndDialogManagerReturnsNullThenControllerIsNotCalled() {
        when(dialogManager.manageDialog("Title")).thenReturn(null);

        panelFixture.button(JButtonMatcher.withText("Add")).click();
        verifyZeroInteractions(courseController);
    }

    @Test
    @GUITest
    public void testWhenDeleteButtonIsClickedThenControllerIsCalled() {
        GuiActionRunner.execute(() -> {
            coursePanel.getListModel().addElement(new Course("title1"));
        });

        panelFixture.list("entityList").selectItem(0);

        panelFixture.button(JButtonMatcher.withText("Delete")).click();
        verify(courseController).deleteEntity(new Course("title1"));

    }

    @Test
    @GUITest
    public void testWhenModifyButtonIsClickedDialogManagerIsCalled() {
        String title = "title1";
        GuiActionRunner.execute(() -> {
            coursePanel.getListModel().addElement(new Course(title));
        });

        panelFixture.list("entityList").selectItem(0);

        panelFixture.button(JButtonMatcher.withText("Modify")).click();
        verify(dialogManager).manageDialog("Title", title);
    }

    @Test
    @GUITest
    public void testWhenModifyButtonIsClickedAndDialogManagerReturnsOutcomeThenControllerIsCalled() {
        String title = "title1";
        String modifiedTitle = "modifiedTitle";
        Course course = new Course(title);

        GuiActionRunner.execute(() -> {
            coursePanel.getListModel().addElement(course);
        });

        panelFixture.list("entityList").selectItem(0);

        when(dialogManager.manageDialog("Title", title))
                .thenReturn(modifiedTitle);

        panelFixture.button(JButtonMatcher.withText("Modify")).click();

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor
                .forClass(Course.class);

        verify(courseController).updateEntity(courseCaptor.capture());
        assertThat(courseCaptor.getValue().getTitle()).isEqualTo(modifiedTitle);
        assertThat(courseCaptor.getValue()).isEqualTo(course);

    }

    @Test
    @GUITest
    public void testWhenModifyButtonIsClickedAndDialogManagerReturnsNullThenControllerIsNotCalled() {
        String title = "title1";

        GuiActionRunner.execute(() -> {
            coursePanel.getListModel().addElement(new Course(title));
        });

        panelFixture.list("entityList").selectItem(0);

        when(dialogManager.manageDialog("Title", title)).thenReturn(null);

        panelFixture.button(JButtonMatcher.withText("Modify")).click();
        verifyZeroInteractions(courseController);
    }

}
