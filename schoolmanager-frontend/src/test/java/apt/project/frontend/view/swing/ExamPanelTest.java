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
import org.mockito.ArgumentCaptor;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Exam;
import apt.project.backend.domain.Student;
import apt.project.frontend.controller.StudentController;
import apt.project.frontend.view.MainFrame;

@RunWith(GUITestRunner.class)
public class ExamPanelTest extends AssertJSwingJUnitTestCase {

    private static final String HEADER_TEXT = "List of Courses";

    private ExamPanel examPanel;

    private JPanel internalPanel;
    private JPanelFixture panelFixture;
    private JFrame frame;
    private DialogManager dialogManager;
    private StudentController studentController;
    private MainFrame mainFrame;
    private Student student;

    @Override
    protected void onSetUp() {

        dialogManager = mock(DialogManager.class);
        mainFrame = mock(MainFrame.class);
        studentController = mock(StudentController.class);
        student = new Student("student");
        GuiActionRunner.execute(() -> {
            internalPanel = new JPanel();
            examPanel = new ExamPanel(internalPanel, mainFrame, dialogManager,
                    HEADER_TEXT);
            examPanel.setController(studentController);
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
        verify(dialogManager).manageDialogExam(asList(exam1, exam2));
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

        when(dialogManager.manageDialogExam(examList))
                .thenReturn(submittedExam);

        // exercise
        panelFixture.button(JButtonMatcher.withText("Add")).click();

        // verify
        List<Exam> modifiedList = new ArrayList<Exam>(student.getExams());
        modifiedList.add(submittedExam);

        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor
                .forClass(Student.class);

        verify(studentController).updateEntity(studentCaptor.capture());
        assertThat(studentCaptor.getValue().getExams())
                .containsAll(modifiedList);
        assertThat(studentCaptor.getValue()).isEqualTo(student);

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

        when(dialogManager.manageDialogExam(examList)).thenReturn(null);

        // exercise
        panelFixture.button(JButtonMatcher.withText("Add")).click();

        // verify
        verifyZeroInteractions(studentController);
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
        List<Exam> modifiedList = new ArrayList<Exam>(asList(exam1));

        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor
                .forClass(Student.class);

        verify(studentController).updateEntity(studentCaptor.capture());
        assertThat(studentCaptor.getValue().getExams())
                .containsAll(modifiedList);
        assertThat(studentCaptor.getValue()).isEqualTo(student);

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
        verify(dialogManager).manageDialog("Rate", rate.toString());
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

        when(dialogManager.manageDialog("Rate", rate.toString()))
                .thenReturn(modifiedRate.toString());

        // exercise
        panelFixture.button(JButtonMatcher.withText("Modify")).click();

        // verify
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor
                .forClass(Student.class);

        verify(studentController).updateEntity(studentCaptor.capture());

        assertThat(studentCaptor.getValue().getExams().get(0).getRate())
                .isEqualTo(modifiedRate);
        assertThat(studentCaptor.getValue().getExams().get(0).getCourse())
                .isEqualTo(course1);
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

        when(dialogManager.manageDialog("Rate", rate.toString()))
                .thenReturn(null);

        // exercise
        panelFixture.button(JButtonMatcher.withText("Modify")).click();

        // verify
        verifyZeroInteractions(studentController);
    }

}
