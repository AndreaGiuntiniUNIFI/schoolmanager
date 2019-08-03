package apt.project.frontend.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

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
public class CustomDialogTest extends AssertJSwingJUnitTestCase {

    private CustomDialog customDialog;
    private DialogFixture myDialogFixture;

    @Override
    protected void onSetUp() {
        GuiActionRunner.execute(() -> {
            customDialog = new CustomDialog("label");
        });

        myDialogFixture = new DialogFixture(robot(), customDialog);

        myDialogFixture.show();
    }

    @Override
    protected void onTearDown() {
        customDialog.setVisible(false);
        customDialog.dispose();
        super.onTearDown();
    }

    @Test
    @GUITest
    public void testControlsInitialStates() {

        myDialogFixture.label(JLabelMatcher.withText("label"));

        myDialogFixture.textBox("labelTextField").requireEnabled()
                .requireEmpty();

        myDialogFixture.button(JButtonMatcher.withText("OK")).requireDisabled();

        myDialogFixture.button(JButtonMatcher.withText("Cancel"))
                .requireEnabled();
    }

    @Test
    @GUITest
    public void testWhenTheFieldIsCompiledOkButtonIsEnabled() {

        myDialogFixture.textBox("labelTextField").enterText("test");

        myDialogFixture.button(JButtonMatcher.withText("OK")).requireEnabled();

    }

    @Test
    @GUITest
    public void testWhenOkButtonIsClickedThenInputIsSavedBeforeClosing() {
        // exercise
        String input = "test";
        myDialogFixture.textBox("labelTextField").enterText(input);
        myDialogFixture.button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(customDialog.getOutcome()).isEqualTo(input);
        assertThat(customDialog.isVisible()).isFalse();
    }

    @Test
    @GUITest
    public void testWhenCancelButtonIsClickedThenDialogIsCLosedWithoutSavingInput() {
        // exercise
        customDialog.setOutcome("outcome");
        String input = "test";
        myDialogFixture.textBox("labelTextField").enterText(input);
        myDialogFixture.button(JButtonMatcher.withText("Cancel")).click();

        // verify
        assertThat(customDialog.getOutcome()).isNull();
        ;
        assertThat(customDialog.isVisible()).isFalse();
    }

}
