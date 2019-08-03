package apt.project.frontend.view.swing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import apt.project.backend.domain.Course;
import apt.project.frontend.controller.CourseController;

@RunWith(GUITestRunner.class)
public class CoursePanelTest extends AssertJSwingJUnitTestCase {

    private JPanelFixture panelFixture;

    private CoursePanel coursePanel;

    private JFrame jframe;

    private DialogManager dialogManager;

    private CourseController courseController;

    @Override
    protected void onSetUp() {

        dialogManager = mock(DialogManager.class);
        courseController = mock(CourseController.class);

        GuiActionRunner.execute(() -> {
            coursePanel = new CoursePanel(dialogManager);
            coursePanel.setCourseController(courseController);
            return coursePanel;
        });

        GuiActionRunner.execute(() -> {
            jframe = new JFrame();
            jframe.getContentPane().add(coursePanel);
            jframe.pack();
            jframe.setVisible(true);
            return jframe;
        });

        panelFixture = new JPanelFixture(robot(), coursePanel);

    }

    @Test
    @GUITest
    public void testControlsInitialStates() {

        panelFixture.label(JLabelMatcher.withText("List of Courses"));
        panelFixture.button(JButtonMatcher.withText("Add")).requireEnabled();
        panelFixture.button(JButtonMatcher.withText("Delete"))
                .requireDisabled();
        panelFixture.button(JButtonMatcher.withText("Modify"))
                .requireDisabled();
        panelFixture.list("coursesList");
    }

    @Test
    @GUITest
    public void testWhenAddButtonIsClickedDialogManagerIsCalled() {
        panelFixture.button(JButtonMatcher.withText("Add")).click();
        verify(dialogManager).manageDialog("Title");
    }

    @Test
    @GUITest
    public void testWhenDialogManagerReturnsOutcomeThenControllerIsCalled() {
        when(dialogManager.manageDialog("Title")).thenReturn("title1");

        panelFixture.button(JButtonMatcher.withText("Add")).click();
        verify(courseController).newEntity(new Course("title1"));
    }

}
