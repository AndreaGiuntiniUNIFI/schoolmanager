package apt.project.frontend.view.swing;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GUITestRunner.class)
public class SwingMainFrameTest extends AssertJSwingJUnitTestCase {

    private SwingMainFrame mainFrame;
    private FrameFixture frameFixture;

    @Override
    protected void onSetUp() {
        GuiActionRunner.execute(() -> {
            mainFrame = new SwingMainFrame();
            frameFixture = new FrameFixture(robot(), mainFrame);
        });
        frameFixture.show();
    }

    @Test
    @GUITest
    public void testInitalFrameContent() {

        frameFixture.tabbedPane("mainTabbedPane");

        GenericTypeMatcher<CoursePanel> coursePanelNameMatcher = new GenericTypeMatcher<CoursePanel>(
                CoursePanel.class) {
            @Override
            protected boolean isMatching(CoursePanel panel) {
                return "coursePanel".equals(panel.getName());
            }
        };

        frameFixture.panel(coursePanelNameMatcher);
        frameFixture.panel("studentPanel");

        frameFixture.label("errorLabel").requireText(" ");
    }

    @Test
    @GUITest
    public void testDisplayErrorLabelSholdShowMessageInErrorLabel() {
        String message = "message";

        GuiActionRunner.execute(() -> mainFrame.displayErrorLabel(message));

        frameFixture.label("errorLabel").requireText(message);
    }

    @Test
    @GUITest
    public void testResetErrorLabelShouldResetErrorLabel() {
        String message = "message";
        GuiActionRunner
                .execute(() -> mainFrame.getErrorLabel().setText(message));

        GuiActionRunner.execute(() -> mainFrame.resetErrorLabel());

        frameFixture.label("errorLabel").requireText(" ");
    }
}
