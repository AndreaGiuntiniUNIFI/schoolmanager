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

public class StudentPanelTest extends AssertJSwingJUnitTestCase {

    private StudentPanel studentPanel;
    private JFrame jframe;
    private JPanelFixture panelFixture;

    @Override
    protected void onSetUp() {

        GuiActionRunner.execute(() -> {
            studentPanel = new StudentPanel();
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
}
