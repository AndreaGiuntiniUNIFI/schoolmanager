package apt.project.frontend.view.swing;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import apt.project.backend.domain.Student;
import apt.project.frontend.view.MainFrame;

public class BasePanelTest extends AssertJSwingJUnitTestCase {

    private static final String HEADER_TEXT = "Base panel";

    private BasePanel<Student> basePanel;

    private JPanel internalPanel;
    private JPanelFixture panelFixture;
    private JFrame frame;
    private MainFrame mainFrame;

    @Override
    protected void onSetUp() {

        DialogManager dialogManager = mock(DialogManager.class);
        mainFrame = mock(MainFrame.class);

        GuiActionRunner.execute(() -> {
            internalPanel = new JPanel();
            basePanel = new BasePanel<Student>(internalPanel, mainFrame,
                    dialogManager, HEADER_TEXT);
            frame = new JFrame();
            frame.add(internalPanel);
            frame.pack();
            frame.setVisible(true);
        });

        panelFixture = new JPanelFixture(robot(), internalPanel);

    }

    @Test
    @GUITest
    public void testControlsInitialStates() {
        panelFixture.label(JLabelMatcher.withText(HEADER_TEXT));
        panelFixture.button(JButtonMatcher.withText("Add")).requireEnabled();
        panelFixture.button(JButtonMatcher.withText("Delete"))
                .requireDisabled();
        panelFixture.button(JButtonMatcher.withText("Modify"))
                .requireDisabled();
        panelFixture.list("entityList");
    }

    @Test
    @GUITest
    public void testWhenEntityIsSelectedDeleteIsEnabled() {
        // setup
        GuiActionRunner.execute(() -> {
            basePanel.getListModel().addElement(new Student("name1"));
        });
        JButtonFixture buttonDelete = panelFixture
                .button(JButtonMatcher.withText("Delete"));

        // exercise
        panelFixture.list("entityList").selectItem(0);
        // verify
        buttonDelete.requireEnabled();

        // exercise
        panelFixture.list("entityList").clearSelection();
        // verify
        buttonDelete.requireDisabled();

    }

    @Test
    @GUITest
    public void testWhenEntityIsSelectedModifyIsEnabled() {
        // setup
        GuiActionRunner.execute(() -> {
            basePanel.getListModel().addElement(new Student("name1"));
        });
        JButtonFixture buttonModify = panelFixture
                .button(JButtonMatcher.withText("Modify"));

        // exercise
        panelFixture.list("entityList").selectItem(0);
        // verify
        buttonModify.requireEnabled();

        // exercise
        panelFixture.list("entityList").clearSelection();
        // verify
        buttonModify.requireDisabled();

    }

    @Test
    @GUITest
    public void testShowAllShouldAddEntitiesToTheList() {
        // setup
        Student student1 = new Student("name1");
        Student student2 = new Student("name2");

        // exercise
        GuiActionRunner
                .execute(() -> basePanel.showAll(asList(student1, student2)));

        // verify
        String[] listContents = panelFixture.list("entityList").contents();
        assertThat(listContents).containsExactly(student1.toString(),
                student2.toString());
    }

    @Test
    @GUITest
    public void testShowErrorShouldCallSetErrorLabelInParent() {
        // setup
        Student student = new Student("name1");

        // exercise
        GuiActionRunner
                .execute(() -> basePanel.showError("error message", student));

        // verify
        verify(basePanel.getParentMainFrame())
                .displayErrorLabel("error message: " + student);
    }

    @Test
    @GUITest
    public void testEntityAddedShouldAddEntityToListAndCallResetErrorLabelInParent() {
        // setup
        Student student = new Student("name1");

        // exercise
        GuiActionRunner.execute(() -> {
            basePanel.entityAdded(student);
        });

        // verify
        String[] listContents = panelFixture.list("entityList").contents();
        assertThat(listContents).containsExactly(student.toString());
        verify(basePanel.getParentMainFrame()).resetErrorLabel();
    }

    @Test
    @GUITest
    public void testEntityDeletedShouldRemoveEntityFromListAndCallResetErrorLabelInParent() {
        // setup
        Student student1 = new Student("name1");
        Student student2 = new Student("name2");
        GuiActionRunner.execute(() -> {
            DefaultListModel<Student> listModel = basePanel.getListModel();
            listModel.addElement(student1);
            listModel.addElement(student2);
        });

        // exercise
        GuiActionRunner
                .execute(() -> basePanel.entityDeleted(new Student("name2")));

        // verify
        String[] listContents = panelFixture.list("entityList").contents();
        assertThat(listContents).containsExactly(student1.toString());
        verify(basePanel.getParentMainFrame()).resetErrorLabel();
    }

    @Test
    @GUITest
    public void testEntityUpdatedShouldUpdateEntityInListAndCallResetErrorLabelInParent() {
        // setup
        Student student = new Student("name1");
        Student modifiedStudent = new Student("modifiedName");
        GuiActionRunner.execute(() -> {
            DefaultListModel<Student> listModel = basePanel.getListModel();
            listModel.addElement(student);
        });

        // exercise
        GuiActionRunner.execute(
                () -> basePanel.entityUpdated(student, modifiedStudent));

        // verify
        String[] listContents = panelFixture.list("entityList").contents();
        assertThat(listContents).containsExactly(modifiedStudent.toString());
        verify(basePanel.getParentMainFrame()).resetErrorLabel();
    }

}
