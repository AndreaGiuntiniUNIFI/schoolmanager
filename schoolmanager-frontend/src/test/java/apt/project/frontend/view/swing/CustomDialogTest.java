package apt.project.frontend.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Dialog;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GUITestRunner.class)
public class CustomDialogTest extends AssertJSwingJUnitTestCase {

    private CustomDialog customDialog;
    private DialogFixture dialogFixture;

    @Override
    protected void onSetUp() {
        GuiActionRunner.execute(() -> {
            customDialog = new CustomDialog();
        });

        dialogFixture = new DialogFixture(robot(), customDialog);

        customDialog.setModalityType(Dialog.ModalityType.MODELESS);

        customDialog.showDialog();

    }

    @Override
    protected void onTearDown() {

        // This should not be necessary, but there is a bug in current Assertj
        // Swing PanelFixture, and this manual cleanup seems to prevent it. See
        // issue #157 on GitHub for further references.
        if (dialogFixture != null) {
            dialogFixture.cleanUp();
        }

        customDialog.setVisible(false);
        customDialog.dispose();
        super.onTearDown();
    }

    @Test
    @GUITest
    public void testControlsInitialStates() {

        dialogFixture.button(JButtonMatcher.withText("OK")).requireDisabled();

        dialogFixture.button(JButtonMatcher.withText("Cancel"))
                .requireEnabled();
    }

    @Test
    @GUITest
    public void testWhenCancelButtonIsClickedThenDialogIsCLosed() {
        // exercise
        dialogFixture.button(JButtonMatcher.withText("Cancel")).click();

        // verify
        assertThat(customDialog.isVisible()).isFalse();
    }

}
