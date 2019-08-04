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

import apt.project.backend.domain.Course;
import apt.project.frontend.controller.CourseController;
import apt.project.frontend.view.MainFrame;

@RunWith(GUITestRunner.class)
public class CoursePanelTest extends AssertJSwingJUnitTestCase {

    private JPanelFixture panelFixture;

    private CoursePanel coursePanel;

    private JFrame jframe;

    private DialogManager dialogManager;

    private MainFrame mainFrame;

    private CourseController courseController;

    @Override
    protected void onSetUp() {

        dialogManager = mock(DialogManager.class);
        courseController = mock(CourseController.class);
        mainFrame = mock(MainFrame.class);

        GuiActionRunner.execute(() -> {
            coursePanel = new CoursePanel(mainFrame, dialogManager);
            coursePanel.setCourseController(courseController);
            return coursePanel;
        });

        GuiActionRunner.execute(() -> {
            jframe = new JFrame();
            jframe.getContentPane().add(coursePanel);
            jframe.pack();
            jframe.setVisible(true);
            return jframe;
        });

        panelFixture = new JPanelFixture(robot(), coursePanel);

    }

    @Test
    @GUITest
    public void testControlsInitialStates() {

        panelFixture.label(JLabelMatcher.withText("List of Courses"));
        panelFixture.button(JButtonMatcher.withText("Add")).requireEnabled();
        panelFixture.button(JButtonMatcher.withText("Delete"))
                .requireDisabled();
        panelFixture.button(JButtonMatcher.withText("Modify"))
                .requireDisabled();
        panelFixture.list("coursesList");
    }

    @Test
    @GUITest
    public void testWhenAddButtonIsClickedDialogManagerIsCalled() {
        panelFixture.button(JButtonMatcher.withText("Add")).click();
        verify(dialogManager).manageDialog("Title");
    }

    @Test
    @GUITest
    public void testWhenAddButtonIsClickedAndDialogManagerReturnsOutcomeThenControllerIsCalled() {
        when(dialogManager.manageDialog("Title")).thenReturn("title1");

        panelFixture.button(JButtonMatcher.withText("Add")).click();
        verify(courseController).newEntity(new Course("title1"));
    }

    @Test
    @GUITest
    public void testWhenAddButtonIsClickedAndDialogManagerReturnsNullThenControllerIsNotCalled() {
        when(dialogManager.manageDialog("Title")).thenReturn(null);

        panelFixture.button(JButtonMatcher.withText("Add")).click();
        verifyZeroInteractions(courseController);
    }

    @Test
    @GUITest
    public void testWhenCourseIsSelectedDeleteIsEnabled() {
        GuiActionRunner.execute(() -> {
            coursePanel.getListModel().addElement(new Course("title1"));
        });

        panelFixture.list("coursesList").selectItem(0);

        JButtonFixture buttonDelete = panelFixture
                .button(JButtonMatcher.withText("Delete"));

        buttonDelete.requireEnabled();

        panelFixture.list("coursesList").clearSelection();

        buttonDelete.requireDisabled();

    }

    @Test
    @GUITest
    public void testWhenCourseIsSelectedModifyIsEnabled() {
        GuiActionRunner.execute(() -> {
            coursePanel.getListModel().addElement(new Course("title1"));
        });

        panelFixture.list("coursesList").selectItem(0);

        JButtonFixture buttonModify = panelFixture
                .button(JButtonMatcher.withText("Modify"));

        buttonModify.requireEnabled();

        panelFixture.list("coursesList").clearSelection();

        buttonModify.requireDisabled();

    }

    @Test
    @GUITest
    public void testWhenDeleteButtonIsClickedThenControllerIsCalled() {
        GuiActionRunner.execute(() -> {
            coursePanel.getListModel().addElement(new Course("title1"));
        });

        panelFixture.list("coursesList").selectItem(0);

        panelFixture.button(JButtonMatcher.withText("Delete")).click();
        verify(courseController).deleteEntity(new Course("title1"));

    }

    @Test
    @GUITest
    public void testWhenModifyButtonIsClickedDialogManagerIsCalled() {
        GuiActionRunner.execute(() -> {
            coursePanel.getListModel().addElement(new Course("title1"));
        });

        panelFixture.list("coursesList").selectItem(0);

        panelFixture.button(JButtonMatcher.withText("Modify")).click();
        verify(dialogManager).manageDialog("Title", new Course("title1"));
    }

    @Test
    @GUITest
    public void testWhenModifyButtonIsClickedAndDialogManagerReturnsOutcomeThenControllerIsCalled() {
        String title = "title1";
        String modifiedTitle = "modifiedTitle";

        GuiActionRunner.execute(() -> {
            coursePanel.getListModel().addElement(new Course(title));
        });

        panelFixture.list("coursesList").selectItem(0);

        when(dialogManager.manageDialog("Title", new Course(title)))
                .thenReturn(modifiedTitle);

        panelFixture.button(JButtonMatcher.withText("Modify")).click();
        verify(courseController).updateEntity(new Course(title),
                new Course(modifiedTitle));
    }

    @Test
    @GUITest
    public void testWhenModifyButtonIsClickedAndDialogManagerReturnsNullThenControllerIsNotCalled() {
        String title = "title1";

        GuiActionRunner.execute(() -> {
            coursePanel.getListModel().addElement(new Course(title));
        });

        panelFixture.list("coursesList").selectItem(0);

        when(dialogManager.manageDialog("Title", new Course(title)))
                .thenReturn(null);

        panelFixture.button(JButtonMatcher.withText("Modify")).click();
        verifyZeroInteractions(courseController);
    }

    @Test
    @GUITest
    public void testShowAllShouldAddCoursesToTheList() {
        Course course1 = new Course("course1");
        Course course2 = new Course("course2");

        GuiActionRunner
                .execute(() -> coursePanel.showAll(asList(course1, course2)));

        String[] listContents = panelFixture.list("coursesList").contents();

        assertThat(listContents).containsExactly(course1.toString(),
                course2.toString());
    }

    @Test
    @GUITest
    public void testShowErrorShouldCallSetErrorLabelInParent() {
        Course course = new Course("course1");
        GuiActionRunner
                .execute(() -> coursePanel.showError("error message", course));
        verify(coursePanel.getParentMainFrame())
                .setErrorLabel("error message: " + course);
    }

    @Test
    @GUITest
    public void testEntityAddedShouldAddCourseToListAndCallResetErrorLabelInParent() {
        Course course = new Course("course1");
        GuiActionRunner.execute(() -> {
            coursePanel.entityAdded(course);
        });

        String[] listContents = panelFixture.list("coursesList").contents();

        assertThat(listContents).containsExactly(course.toString());
        verify(coursePanel.getParentMainFrame()).resetErrorLabel();
    }

    @Test
    @GUITest
    public void testEntityDeletedShouldRemoveCourseFromListAndCallResetErrorLabelInParent() {
        Course course1 = new Course("course1");
        Course course2 = new Course("course2");
        GuiActionRunner.execute(() -> {
            DefaultListModel<Course> listModel = coursePanel.getListModel();
            listModel.addElement(course1);
            listModel.addElement(course2);
        });

        GuiActionRunner.execute(
                () -> coursePanel.entityDeleted(new Course("course2")));

        String[] listContents = panelFixture.list("coursesList").contents();
        assertThat(listContents).containsExactly(course1.toString());
        verify(coursePanel.getParentMainFrame()).resetErrorLabel();
    }

    @Test
    @GUITest
    public void testEntityUpdatedShouldUpdateCourseInListAndCallResetErrorLabelInParent() {
        Course course = new Course("course1");
        Course modifiedCourse = new Course("modifiedCourse1");
        GuiActionRunner.execute(() -> {
            DefaultListModel<Course> listModel = coursePanel.getListModel();
            listModel.addElement(course);
        });

        GuiActionRunner.execute(
                () -> coursePanel.entityUpdated(course, modifiedCourse));

        String[] listContents = panelFixture.list("coursesList").contents();
        assertThat(listContents).containsExactly(modifiedCourse.toString());
        verify(coursePanel.getParentMainFrame()).resetErrorLabel();
    }
}
