package apt.project.frontend.view.swing;

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

@RunWith(GUITestRunner.class)
public class CoursePanelTest extends AssertJSwingJUnitTestCase {

    private JPanelFixture panelFixture;

    private CoursePanel coursePanel;

    private JFrame jframe;

    @Override
    protected void onSetUp() {
        GuiActionRunner.execute(() -> {
            coursePanel = new CoursePanel();
            return coursePanel;
        });

        GuiActionRunner.execute(() -> {
            jframe = new JFrame();
            jframe.getContentPane().add(coursePanel);
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

}
