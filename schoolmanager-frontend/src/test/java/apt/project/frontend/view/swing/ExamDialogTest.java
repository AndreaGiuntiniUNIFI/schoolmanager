package apt.project.frontend.view.swing;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

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
import apt.project.backend.domain.Exam;

@RunWith(GUITestRunner.class)
public class ExamDialogTest extends AssertJSwingJUnitTestCase {

    private ExamDialog dialog;
    private DialogFixture dialogFixture;
    private Course course;

    @Override
    protected void onSetUp() {

        GuiActionRunner.execute(() -> {
            dialog = new ExamDialog();
        });

        dialogFixture = new DialogFixture(robot(), dialog);
        dialog.setModalityType(Dialog.ModalityType.MODELESS);

        course = new Course("course2");
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

            dialog.getExamComboBox().addItem((course).toString());
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

            dialog.getExamComboBox().addItem((course).toString());
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

    @Test
    @GUITest
    public void testsShowAllStudentsShouldAddStudentDescriptionsToTheList() {

        Course course1 = new Course("course1");
        Course course2 = new Course("course2");
        GuiActionRunner.execute(
                () -> dialog.setCoursesComboBox(asList(course1, course2)));
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
        assertThat(dialog.getOutcome().getRate()).isEqualTo(19);
        assertThat(dialog.isVisible()).isFalse();
    }

    @Test
    @GUITest
    public void testWhenCancelButtonIsClickedThenOutcomeIsNull() {
        // setup
        Exam outcome = new Exam(course, 20);
        dialog.setOutcome(outcome);
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
        dialogFixture.button(JButtonMatcher.withText("Cancel")).click();

        // verify
        assertThat(dialog.getOutcome()).isNull();
    }

}
