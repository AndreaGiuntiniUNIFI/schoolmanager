package apt.project.frontend.view.swing.dialog;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Dialog;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GUITestRunner.class)
public class SimpleDialogTest extends AssertJSwingJUnitTestCase {

    private SimpleDialog dialog;
    private DialogFixture dialogFixture;

    @Override
    protected void onSetUp() {
        GuiActionRunner.execute(() -> dialog = new SimpleDialog("label"));
        dialogFixture = new DialogFixture(robot(), dialog);
        dialog.setModalityType(Dialog.ModalityType.MODELESS);
        dialog.showDialog();
    }

    @Override
    protected void onTearDown() {

        // This should not be necessary, but there is a bug in current Assertj
        // Swing PanelFixture, and this manual cleanup seems to prevent it. See
        // issue #157 on GitHub for further references.
        if (dialogFixture != null) {
            dialogFixture.cleanUp();
        }

        dialog.setVisible(false);
        dialog.dispose();
        super.onTearDown();
    }

    @Test
    @GUITest
    public void testControlsInitialStates() {
        // verify
        dialogFixture.label(JLabelMatcher.withText("label"));
        dialogFixture.textBox("labelTextField").requireEnabled().requireEmpty();
        dialogFixture.button(JButtonMatcher.withText("OK")).requireDisabled();
        dialogFixture.button(JButtonMatcher.withText("Cancel"))
                .requireEnabled();
    }

    @Test
    @GUITest
    public void testWhenTheFieldIsCompiledOkButtonIsEnabled() {
        // setup
        dialogFixture.textBox("labelTextField").enterText("test");

        // verify
        dialogFixture.button(JButtonMatcher.withText("OK")).requireEnabled();

    }

    @Test
    @GUITest
    public void testWhenOkButtonIsClickedThenInputIsSavedBeforeClosing() {
        // setup
        String input = "test";
        dialogFixture.textBox("labelTextField").enterText(input);

        // exercise
        dialogFixture.button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(dialog.getOutcome()).isEqualTo(input);
        assertThat(dialog.isVisible()).isFalse();
    }

    @Test
    @GUITest
    public void testWhenCancelButtonIsClickedThenOutcomeIsNull() {
        // setup
        dialog.setOutcome("outcome");
        String input = "test";
        dialogFixture.textBox("labelTextField").enterText(input);

        // exercise
        dialogFixture.button(JButtonMatcher.withText("Cancel")).click();

        // verify
        assertThat(dialog.getOutcome()).isNull();
        assertThat(dialog.isVisible()).isFalse();
    }

    @Test
    @GUITest
    public void testControlsInitialStatesWithDefaultValue() {
        // clean up the fixture created on setup
        dialogFixture.cleanUp();
        dialog.setVisible(false);
        dialog.dispose();
        dialog.setVisible(false);

        // create new fixture with dialog with default value
        String defaultVal = "default";
        GuiActionRunner
                .execute(() -> dialog = new SimpleDialog("label", defaultVal));
        dialogFixture = new DialogFixture(robot(), dialog);
        dialog.setModalityType(Dialog.ModalityType.MODELESS);
        dialog.showDialog();

        // verify
        dialogFixture.label(JLabelMatcher.withText("label"));
        dialogFixture.textBox("labelTextField").requireEnabled()
                .requireText(defaultVal);
        dialogFixture.button(JButtonMatcher.withText("OK")).requireEnabled();
        dialogFixture.button(JButtonMatcher.withText("Cancel"))
                .requireEnabled();
    }

}
