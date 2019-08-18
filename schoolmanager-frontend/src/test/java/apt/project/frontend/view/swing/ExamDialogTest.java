package apt.project.frontend.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JComboBox;

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

import apt.project.backend.domain.Course;
import apt.project.frontend.controller.CustomDialogController;

@RunWith(GUITestRunner.class)
public class ExamDialogTest extends AssertJSwingJUnitTestCase {

    private ExamDialog dialog;
    private DialogFixture dialogFixture;
    private CustomDialogController customDialogController;

    @Override
    protected void onSetUp() {

        GuiActionRunner.execute(() -> {
            dialog = new ExamDialog();
        });

        customDialogController = mock(CustomDialogController.class);
        dialogFixture = new DialogFixture(robot(), dialog);
        dialog.setModalityType(Dialog.ModalityType.MODELESS);
        dialog.setController(customDialogController);

        GuiActionRunner.execute(() -> {
            dialog.showDialog();
        });

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
    public void testControlInitialStates() {

        dialogFixture.label(JLabelMatcher.withText("Exam"));
        dialogFixture.label(JLabelMatcher.withText("Rate"));

        dialogFixture.comboBox("examComboBox");
        dialogFixture.comboBox("rateComboBox");

        List<String> availableRate = new ArrayList<>();
        IntStream.range(18, 31).boxed()
                .forEach(i -> availableRate.add(i.toString()));

        assertThat(dialogFixture.comboBox("rateComboBox").contents())
                .isEqualTo(availableRate.toArray());

    }

    void unselect(JComboBox<String> comboBox) {
        GuiActionRunner.execute(() -> {
            comboBox.setSelectedIndex(-1);
        });

    }

    @Test
    public void testWhenCourseAndRateAreSelectedThenOkButtonShouldBeEnabled() {
        GuiActionRunner.execute(() -> {
            dialog.getExamComboBox()
                    .addItem((new Course("course1")).toString());

            dialog.getExamComboBox()
                    .addItem((new Course("course2")).toString());
        });

        dialogFixture.comboBox("examComboBox").selectItem(1);
        dialogFixture.comboBox("rateComboBox").selectItem(1);
        dialogFixture.button(JButtonMatcher.withText("OK")).requireEnabled();
    }

    @Test
    @GUITest
    public void testWhenWhenEitherCourseOrRateAreNotSelectedThenOkButtonShouldBeDisabled() {
        // setup
        GuiActionRunner.execute(() -> {
            dialog.getExamComboBox()
                    .addItem((new Course("course1")).toString());

            dialog.getExamComboBox()
                    .addItem((new Course("course2")).toString());
        });

        JComboBoxFixture examComboBox = dialogFixture.comboBox("examComboBox");
        JComboBoxFixture rateComboBox = dialogFixture.comboBox("rateComboBox");

        // verify
        examComboBox.selectItem(1);
        unselect(dialog.getRateComboBox());
        dialogFixture.button(JButtonMatcher.withText("OK")).requireDisabled();

        unselect(dialog.getExamComboBox());
        unselect(dialog.getRateComboBox());
        dialogFixture.button(JButtonMatcher.withText("OK")).requireDisabled();

        unselect(dialog.getExamComboBox());
        rateComboBox.selectItem(1);
        dialogFixture.button(JButtonMatcher.withText("OK")).requireDisabled();

    }

}
