package apt.project.frontend.view.swing;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Exam;
import apt.project.backend.domain.Student;
import apt.project.frontend.controller.ExamController;
import apt.project.frontend.controller.ExamDialogController;
import apt.project.frontend.view.MainFrame;
import apt.project.frontend.view.swing.dialog.DialogManager;

@RunWith(GUITestRunner.class)
public class ExamPanelTest extends AssertJSwingJUnitTestCase {

    private static final String HEADER_TEXT = "List of Exams";

    private ExamPanel examPanel;

    private JPanel internalPanel;
    private JPanelFixture panelFixture;
    private JFrame frame;
    private DialogManager dialogManager;
    private ExamController examController;
    private ExamDialogController examDialogController;
    private MainFrame mainFrame;
    private Student student;

    @Override
    protected void onSetUp() {

        dialogManager = mock(DialogManager.class);
        mainFrame = mock(MainFrame.class);
        examController = mock(ExamController.class);
        student = new Student("student");
        GuiActionRunner.execute(() -> {
            internalPanel = new JPanel();
            examPanel = new ExamPanel(internalPanel, dialogManager,
                    HEADER_TEXT);
            examPanel.setMainFrame(mainFrame);
            examPanel.setController(examController);
            examPanel.setExamDialogController(examDialogController);
            frame = new JFrame();
            frame.add(internalPanel);
            frame.pack();
            frame.setVisible(true);
        });

        panelFixture = new JPanelFixture(robot(), internalPanel);
    }

    @Test
    @GUITest
    public void testWhenShowAllShouldShowAllExamOfTheStudent() {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 28);
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 24);
        student.setExams(asList(exam1, exam2));
        examPanel.setStudent(student);

        // exercise
        GuiActionRunner.execute(() -> examPanel.showAll());

        // verify
        String[] listContents = panelFixture.list("entityList").contents();
        assertThat(listContents).containsExactly(exam1.toString(),
                exam2.toString());
    }

    @Test
    @GUITest
    public void testControlsInitialStates() {
        panelFixture.button(JButtonMatcher.withText("Back")).requireEnabled();
    }

    @Test
    @GUITest
    public void testWhenAddButtonIsClickedDialogManagerIsCalled() {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 28);
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 24);
        student.setExams(asList(exam1, exam2));
        examPanel.setStudent(student);

        // exercise
        panelFixture.button(JButtonMatcher.withText("Add")).click();

        // verify
        verify(dialogManager).manageExamDialog(asList(course1, course2),
                examDialogController);
    }

    @Test
    @GUITest
    public void testWhenAddButtonIsClickedAndDialogManagerReturnsOutcomeThenControllerIsCalled() {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 28);

        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 24);

        List<Exam> examList = new ArrayList<>(asList(exam1, exam2));

        student.setExams(examList);
        examPanel.setStudent(student);

        Course course3 = new Course("course3");
        Exam submittedExam = new Exam(course3, 30);

        when(dialogManager.manageExamDialog(asList(course1, course2),
                examDialogController)).thenReturn(submittedExam);

        // exercise
        panelFixture.button(JButtonMatcher.withText("Add")).click();

        // verify
        List<Exam> modifiedList = new ArrayList<Exam>(student.getExams());
        modifiedList.add(submittedExam);

        verify(examController).newEntity(student, submittedExam);
    }

    @Test
    @GUITest
    public void testWhenAddButtonIsClickedAndDialogManagerReturnsNullThenControllerIsNotCalled() {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 28);
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 24);
        List<Exam> examList = asList(exam1, exam2);
        student.setExams(examList);
        examPanel.setStudent(student);

        when(dialogManager.manageExamDialog(asList(course1, course2),
                examDialogController)).thenReturn(null);

        // exercise
        panelFixture.button(JButtonMatcher.withText("Add")).click();

        // verify
        verifyZeroInteractions(examController);
    }

    @Test
    @GUITest
    public void testWhenDeleteButtonIsClickedThenControllerIsCalled() {
        // setUp
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 28);
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 24);
        List<Exam> examList = new ArrayList<>(asList(exam1, exam2));

        student.setExams(examList);
        examPanel.setStudent(student);

        GuiActionRunner.execute(() -> {
            examPanel.getListModel().addElement(exam1);
            examPanel.getListModel().addElement(exam2);
        });

        panelFixture.list("entityList").selectItem(1);

        // exercise
        panelFixture.button(JButtonMatcher.withText("Delete")).click();

        // verify
        verify(examController).deleteEntity(student, exam2);

    }

    @Test
    @GUITest
    public void testWhenModifyButtonIsClickedDialogManagerIsCalled() {
        // setUp
        Course course1 = new Course("course1");
        Integer rate = 28;
        Exam exam1 = new Exam(course1, rate);
        List<Exam> examList = new ArrayList<>(asList(exam1));

        student.setExams(examList);
        examPanel.setStudent(student);

        GuiActionRunner.execute(() -> {
            examPanel.getListModel().addElement(exam1);
        });

        panelFixture.list("entityList").selectItem(0);

        // exercise
        panelFixture.button(JButtonMatcher.withText("Modify")).click();

        // verify
        verify(dialogManager).manageSimpleExamDialog();
    }

    @Test
    @GUITest
    public void testWhenModifyButtonIsClickedAndDialogManagerReturnsOutcomeThenControllerIsCalled() {
        // setUp
        Course course1 = new Course("course1");
        Integer rate = 28;
        Integer modifiedRate = 30;
        Exam exam1 = new Exam(course1, rate);
        List<Exam> examList = new ArrayList<>(asList(exam1));

        student.setExams(examList);
        examPanel.setStudent(student);

        GuiActionRunner.execute(() -> {
            examPanel.getListModel().addElement(exam1);
        });

        panelFixture.list("entityList").selectItem(0);

        when(dialogManager.manageSimpleExamDialog())
                .thenReturn(modifiedRate.toString());

        // exercise
        panelFixture.button(JButtonMatcher.withText("Modify")).click();

        // verify
        verify(examController).updateEntity(student, exam1);
    }

    @Test
    @GUITest
    public void testWhenModifyButtonIsClickedAndDialogManagerReturnsNullThenControllerIsNotCalled() {
        // setUp
        Course course1 = new Course("course1");
        Integer rate = 28;
        Exam exam1 = new Exam(course1, rate);
        List<Exam> examList = new ArrayList<>(asList(exam1));

        student.setExams(examList);
        examPanel.setStudent(student);

        GuiActionRunner.execute(() -> {
            examPanel.getListModel().addElement(exam1);
        });

        panelFixture.list("entityList").selectItem(0);

        when(dialogManager.manageSimpleExamDialog()).thenReturn(null);

        // exercise
        panelFixture.button(JButtonMatcher.withText("Modify")).click();

        // verify
        verifyZeroInteractions(examController);
    }

}
