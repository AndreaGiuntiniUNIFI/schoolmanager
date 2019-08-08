package apt.project.frontend.view.swing;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import apt.project.backend.domain.Student;
import apt.project.frontend.controller.StudentController;
import apt.project.frontend.view.MainFrame;

@RunWith(GUITestRunner.class)

public class StudentPanelTest extends AssertJSwingJUnitTestCase {

    private StudentPanel studentPanel;
    private JFrame jframe;
    private JPanelFixture panelFixture;
    private DialogManager dialogManager;
    private StudentController studentController;
    private MainFrame mainFrame;

    @Override
    protected void onSetUp() {

        GuiActionRunner.execute(() -> {
            dialogManager = mock(DialogManager.class);
            studentController = mock(StudentController.class);
            mainFrame = mock(MainFrame.class);
            studentPanel = new StudentPanel(mainFrame, dialogManager);
            studentPanel.setStudentController(studentController);
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

    @Test
    @GUITest
    public void testWhenAddButtonIsClickedDialogManagerIsCalled() {
        panelFixture.button(JButtonMatcher.withText("Add")).click();
        verify(dialogManager).manageDialog("Name");
    }

    @Test
    @GUITest
    public void testWhenAddButtonIsClickedAndDialogManagerReturnsOutcomeThenControllerIsCalled() {
        when(dialogManager.manageDialog("Name")).thenReturn("name1");

        panelFixture.button(JButtonMatcher.withText("Add")).click();
        verify(studentController).newEntity(new Student("name1"));
    }

    @Test
    @GUITest
    public void testWhenAddButtonIsClickedAndDialogManagerReturnsNullThenControllerIsNotCalled() {
        when(dialogManager.manageDialog("Name")).thenReturn(null);

        panelFixture.button(JButtonMatcher.withText("Add")).click();
        verifyZeroInteractions(studentController);
    }

    @Test
    @GUITest
    public void testWhenStudentIsSelectedDeleteIsEnabled() {
        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(new Student("name1"));
        });

        panelFixture.list("studentList").selectItem(0);

        JButtonFixture buttonDelete = panelFixture
                .button(JButtonMatcher.withText("Delete"));

        buttonDelete.requireEnabled();

        panelFixture.list("studentList").clearSelection();

        buttonDelete.requireDisabled();

    }

    @Test
    @GUITest
    public void testWhenStudentIsSelectedModifyIsEnabled() {
        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(new Student("name1"));
        });

        panelFixture.list("studentList").selectItem(0);

        JButtonFixture buttonModify = panelFixture
                .button(JButtonMatcher.withText("Modify"));

        buttonModify.requireEnabled();

        panelFixture.list("studentList").clearSelection();

        buttonModify.requireDisabled();

    }

    @Test
    @GUITest
    public void testWhenDeleteButtonIsClickedThenControllerIsCalled() {
        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(new Student("name1"));
        });

        panelFixture.list("studentList").selectItem(0);

        panelFixture.button(JButtonMatcher.withText("Delete")).click();
        verify(studentController).deleteEntity(new Student("name1"));

    }

    @Test
    @GUITest
    public void testWhenModifyButtonIsClickedDialogManagerIsCalled() {
        String name = "name1";
        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(new Student(name));
        });

        panelFixture.list("studentList").selectItem(0);

        panelFixture.button(JButtonMatcher.withText("Modify")).click();
        verify(dialogManager).manageDialog("Name", name);
    }

    @Test
    @GUITest
    public void testWhenModifyButtonIsClickedAndDialogManagerReturnsOutcomeThenControllerIsCalled() {
        String name = "name1";
        String modifiedName = "modifiedName";

        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(new Student(name));
        });

        panelFixture.list("studentList").selectItem(0);

        when(dialogManager.manageDialog("Name", name)).thenReturn(modifiedName);

        panelFixture.button(JButtonMatcher.withText("Modify")).click();
        verify(studentController).updateEntity(new Student(name),
                new Student(modifiedName));
    }

    @Test
    @GUITest
    public void testWhenModifyButtonIsClickedAndDialogManagerReturnsNullThenControllerIsNotCalled() {
        String name = "name1";

        GuiActionRunner.execute(() -> {
            studentPanel.getListModel().addElement(new Student(name));
        });

        panelFixture.list("studentList").selectItem(0);

        when(dialogManager.manageDialog("Name", name)).thenReturn(null);

        panelFixture.button(JButtonMatcher.withText("Modify")).click();
        verifyZeroInteractions(studentController);
    }

    @Test
    @GUITest
    public void testShowAllShouldAddStudentsToTheList() {
        Student student1 = new Student("name1");
        Student student2 = new Student("name2");

        GuiActionRunner.execute(
                () -> studentPanel.showAll(asList(student1, student2)));

        String[] listContents = panelFixture.list("studentList").contents();

        assertThat(listContents).containsExactly(student1.toString(),
                student2.toString());
    }

    @Test
    @GUITest
    public void testShowErrorShouldCallSetErrorLabelInParent() {
        Student student = new Student("name1");
        GuiActionRunner.execute(
                () -> studentPanel.showError("error message", student));
        verify(studentPanel.getParentMainFrame())
                .displayErrorLabel("error message: " + student);
    }

    @Test
    @GUITest
    public void testEntityAddedShouldAddCourseToListAndCallResetErrorLabelInParent() {
        Student student = new Student("name1");
        GuiActionRunner.execute(() -> {
            studentPanel.entityAdded(student);
        });

        String[] listContents = panelFixture.list("studentList").contents();

        assertThat(listContents).containsExactly(student.toString());
        verify(studentPanel.getParentMainFrame()).resetErrorLabel();
    }

    @Test
    @GUITest
    public void testEntityDeletedShouldRemoveCourseFromListAndCallResetErrorLabelInParent() {
        Student student1 = new Student("name1");
        Student student2 = new Student("name2");
        GuiActionRunner.execute(() -> {
            DefaultListModel<Student> listModel = studentPanel.getListModel();
            listModel.addElement(student1);
            listModel.addElement(student2);
        });

        GuiActionRunner.execute(
                () -> studentPanel.entityDeleted(new Student("name2")));

        String[] listContents = panelFixture.list("studentList").contents();
        assertThat(listContents).containsExactly(student1.toString());
        verify(studentPanel.getParentMainFrame()).resetErrorLabel();
    }

    @Test
    @GUITest
    public void testEntityUpdatedShouldUpdateCourseInListAndCallResetErrorLabelInParent() {
        Student student = new Student("name1");
        Student modifiedStudent = new Student("modifiedName");
        GuiActionRunner.execute(() -> {
            DefaultListModel<Student> listModel = studentPanel.getListModel();
            listModel.addElement(student);
        });

        GuiActionRunner.execute(
                () -> studentPanel.entityUpdated(student, modifiedStudent));

        String[] listContents = panelFixture.list("studentList").contents();
        assertThat(listContents).containsExactly(modifiedStudent.toString());
        verify(studentPanel.getParentMainFrame()).resetErrorLabel();
    }

}
