package apt.project.frontend.view.swing.dialog;

import static java.util.Arrays.asList;
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

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Exam;

@RunWith(GUITestRunner.class)
public class ExamDialogTest extends AssertJSwingJUnitTestCase {

    private ExamDialog dialog;
    private DialogFixture dialogFixture;

    @Override
    protected void onSetUp() {
        GuiActionRunner.execute(() -> dialog = new ExamDialog());

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
    public void testControlInitialStates() {
        // setup
        String[] availableRates = IntStream.range(18, 31).boxed()
                .map(String::valueOf).toArray(String[]::new);

        // verify
        dialogFixture.label(JLabelMatcher.withText("Exam"));
        dialogFixture.label(JLabelMatcher.withText("Rate"));

        JComboBoxFixture rateComboBoxFixture = dialogFixture
                .comboBox("rateComboBox");
        JComboBoxFixture examComboBoxFixture = dialogFixture
                .comboBox("examComboBox");

        rateComboBoxFixture.requireNoSelection();
        examComboBoxFixture.requireNoSelection();
        assertThat(rateComboBoxFixture.contents()).isEqualTo(availableRates);

        dialogFixture.button(JButtonMatcher.withText("OK")).requireDisabled();
        dialogFixture.button(JButtonMatcher.withText("Cancel"))
                .requireEnabled();
    }

    @Test
    public void testWhenCourseAndRateAreSelectedThenOkButtonShouldBeEnabled() {
        // setup
        GuiActionRunner.execute(() -> {
            dialog.getExamComboBox().addItem(new Course("course1").toString());
            dialog.getExamComboBox().addItem(new Course("course2").toString());
        });

        // verify
        dialogFixture.comboBox("examComboBox").selectItem(1);
        dialogFixture.comboBox("rateComboBox").selectItem(1);
        dialogFixture.button(JButtonMatcher.withText("OK")).requireEnabled();
    }

    @Test
    @GUITest
    public void testWhenWhenEitherCourseOrRateAreNotSelectedThenOkButtonShouldBeDisabled() {
        // setup
        GuiActionRunner.execute(() -> {
            dialog.getExamComboBox().addItem(new Course("course1").toString());
            dialog.getExamComboBox().addItem(new Course("course2").toString());
        });

        JComboBoxFixture examComboBoxFixture = dialogFixture
                .comboBox("examComboBox");
        JComboBoxFixture rateComboBoxFixture = dialogFixture
                .comboBox("rateComboBox");

        // verify
        examComboBoxFixture.selectItem(1);
        rateComboBoxFixture.clearSelection();
        dialogFixture.button(JButtonMatcher.withText("OK")).requireDisabled();

        examComboBoxFixture.clearSelection();
        rateComboBoxFixture.selectItem(1);
        dialogFixture.button(JButtonMatcher.withText("OK")).requireDisabled();

    }

    @Test
    @GUITest
    public void testSetCoursesComboBoxShouldAddStudentDescriptionsToComboBoxChoicesAndCoursesList() {
        // setup
        Course course1 = new Course("course1");
        Course course2 = new Course("course2");

        // exercise
        GuiActionRunner.execute(
                () -> dialog.setCoursesComboBox(asList(course1, course2)));

        // verify
        String[] listContents = dialogFixture.comboBox("examComboBox")
                .contents();
        assertThat(listContents).containsExactly(course1.toString(),
                course2.toString());
        assertThat(dialog.getCourses()).containsExactly(course1, course2);

    }

    @Test
    @GUITest
    public void testWhenOkButtonIsClickedThenInputIsSavedBeforeClosing() {
        // setup
        Course course1 = new Course("course1");
        Course course2 = new Course("course2");

        GuiActionRunner.execute(() -> {
            dialog.getExamComboBox().addItem(course1.toString());
            dialog.getExamComboBox().addItem(course2.toString());
            dialog.setCourses(asList(course1, course2));
        });

        // exercise
        dialogFixture.comboBox("examComboBox").selectItem(1);
        dialogFixture.comboBox("rateComboBox").selectItem(1);
        dialogFixture.button(JButtonMatcher.withText("OK")).click();

        // verify
        assertThat(dialog.getOutcome().getCourse()).isEqualTo(course2);
        assertThat(dialog.getOutcome().getRate()).isEqualTo(19); // TODO:
                                                                 // prendere
                                                                 // 19 da
                                                                 // fixture?
        assertThat(dialog.isVisible()).isFalse();
    }

    @Test
    @GUITest
    public void testWhenCancelButtonIsClickedThenOutcomeIsNull() {
        // setup
        Course course1 = new Course("course1");
        Course course2 = new Course("course2");

        Exam previousOutcome = new Exam(new Course("previous"), 20);
        dialog.setOutcome(previousOutcome);

        GuiActionRunner.execute(() -> {
            dialog.getExamComboBox().addItem(course1.toString());
            dialog.getExamComboBox().addItem(course2.toString());
            dialog.setCourses(asList(course1, course2));
        });

        // exercise
        dialogFixture.comboBox("examComboBox").selectItem(1);
        dialogFixture.comboBox("rateComboBox").selectItem(1);
        dialogFixture.button(JButtonMatcher.withText("Cancel")).click();

        // verify
        assertThat(dialog.getOutcome()).isNull();
    }

}
