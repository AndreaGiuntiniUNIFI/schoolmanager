package apt.project.frontend.view.swing;

import javax.swing.JPanel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import apt.project.frontend.controller.ExamController;
import apt.project.frontend.view.swing.dialog.DialogManager;

@RunWith(GUITestRunner.class)
public class SwingMainFrameTest extends AssertJSwingJUnitTestCase {

    private SwingMainFrame mainFrame;
    private FrameFixture frameFixture;

    @Override
    protected void onSetUp() {
        GuiActionRunner.execute(() -> {
            DialogManager dialogManager = new DialogManager();

            ExamPanel examPanel = new ExamPanel(new JPanel(), dialogManager,
                    "List of Exams");

            ExamController examController = new ExamController(examPanel, null);

            StudentPanel studentPanel = new StudentPanel(new JPanel(),
                    examPanel, dialogManager, examController,
                    "List of Students");

            CoursePanel coursePanel = new CoursePanel(new JPanel(),
                    dialogManager, "List of Courses");

            mainFrame = new SwingMainFrame(coursePanel, studentPanel);

            examPanel.setMainFrame(mainFrame);
            coursePanel.setMainFrame(mainFrame);
            studentPanel.setMainFrame(mainFrame);

            frameFixture = new FrameFixture(robot(), mainFrame);
        });
        frameFixture.show();
    }

    @Test
    @GUITest
    public void testInitalFrameContent() {

        frameFixture.tabbedPane("mainTabbedPane").requireTabTitles("Students",
                "Courses");

        frameFixture.tabbedPane("mainTabbedPane").selectTab(0);
        frameFixture.panel("studentPanel");
        frameFixture.tabbedPane("mainTabbedPane").selectTab(1);
        frameFixture.panel("coursePanel");

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
