package apt.project.frontend.view.swing.dialog;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Dialog;
import java.util.stream.IntStream;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.JComboBoxFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GUITestRunner.class)
public class SimpleExamDialogTest extends AssertJSwingJUnitTestCase {

    private SimpleExamDialog dialog;
    private DialogFixture dialogFixture;

    @Override
    protected void onSetUp() {
        GuiActionRunner.execute(() -> dialog = new SimpleExamDialog());

        dialogFixture = new DialogFixture(robot(), dialog);
        dialog.setModalityType(Dialog.ModalityType.MODELESS);
        GuiActionRunner.execute(() -> dialog.showDialog());
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
        // setup
        String[] availableRates = IntStream.range(18, 31).boxed()
                .map(String::valueOf).toArray(String[]::new);

        // verify
        dialogFixture.label(JLabelMatcher.withText("Rate"));

        JComboBoxFixture comboBoxFixture = dialogFixture
                .comboBox("rateComboBox");
        comboBoxFixture.requireNoSelection();
        assertThat(comboBoxFixture.contents()).isEqualTo(availableRates);

        dialogFixture.button(JButtonMatcher.withText("OK")).requireDisabled();
        dialogFixture.button(JButtonMatcher.withText("Cancel"))
                .requireEnabled();
    }

    @Test
    @GUITest
    public void testWhenComboBoxHasSelectionOkButtonIsEnabled() {
        // setup
        dialogFixture.comboBox("rateComboBox").selectItem(1);

        // verify
        dialogFixture.button(JButtonMatcher.withText("OK")).requireEnabled();
    }

    @Test
    @GUITest
    public void testWhenOkButtonIsClickedThenInputIsSavedBeforeClosing() {
        // setup
        JComboBoxFixture comboBoxFixture = dialogFixture
                .comboBox("rateComboBox");
        comboBoxFixture.selectItem(3);
        String selectedValue = comboBoxFixture.selectedItem();

        // exercise
        dialogFixture.button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(dialog.getOutcome()).isEqualTo(selectedValue);
        assertThat(dialog.isVisible()).isFalse();
    }

    @Test
    @GUITest
    public void testWhenCancelButtonIsClickedThenOutcomeIsNull() {
        // setup
        JComboBoxFixture comboBoxFixture = dialogFixture
                .comboBox("rateComboBox");
        comboBoxFixture.selectItem(3);

        // exercise
        dialogFixture.button(JButtonMatcher.withText("Cancel")).click();

        // verify
        assertThat(dialog.getOutcome()).isNull();
        assertThat(dialog.isVisible()).isFalse();
    }

}
