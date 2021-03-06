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

import apt.project.backend.domain.BaseEntity;
import apt.project.frontend.testdomain.TestEntity;
import apt.project.frontend.view.MainFrame;

public class BasePanelTest extends AssertJSwingJUnitTestCase {

    private static final String HEADER_TEXT = "Base panel";

    private BasePanel<BaseEntity> basePanel;

    private JPanel internalPanel;
    private JPanelFixture panelFixture;
    private JFrame frame;
    private MainFrame mainFrame;

    @Override
    protected void onSetUp() {

        mainFrame = mock(MainFrame.class);

        GuiActionRunner.execute(() -> {
            internalPanel = new JPanel();
            basePanel = new BasePanel<>(internalPanel, HEADER_TEXT);
            basePanel.setMainFrame(mainFrame);
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
            basePanel.getListModel().addElement(new TestEntity());
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
            basePanel.getListModel().addElement(new TestEntity());
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
        TestEntity entity1 = new TestEntity();
        TestEntity entity2 = new TestEntity();

        // exercise
        GuiActionRunner
                .execute(() -> basePanel.showAll(asList(entity1, entity2)));

        // verify
        String[] listContents = panelFixture.list("entityList").contents();
        assertThat(listContents).containsExactly(entity1.toString(),
                entity2.toString());
    }

    @Test
    @GUITest
    public void testShowErrorShouldCallSetErrorLabelInParent() {
        // exercise
        GuiActionRunner.execute(() -> basePanel.showError("error message"));

        // verify
        verify(basePanel.getMainFrame()).displayErrorLabel("error message");
    }

    @Test
    @GUITest
    public void testEntityAddedShouldAddEntityToListAndCallResetErrorLabelInParent() {
        // setup
        TestEntity entity = new TestEntity();

        // exercise
        GuiActionRunner.execute(() -> {
            basePanel.entityAdded(entity);
        });

        // verify
        String[] listContents = panelFixture.list("entityList").contents();
        assertThat(listContents).containsExactly(entity.toString());
        verify(basePanel.getMainFrame()).resetErrorLabel();
    }

    @Test
    @GUITest
    public void testEntityDeletedShouldRemoveEntityFromListAndCallResetErrorLabelInParent() {
        // setup
        TestEntity entity1 = new TestEntity();
        TestEntity entity2 = new TestEntity();
        GuiActionRunner.execute(() -> {
            DefaultListModel<BaseEntity> listModel = basePanel.getListModel();
            listModel.addElement(entity1);
            listModel.addElement(entity2);
        });

        // exercise
        GuiActionRunner
                .execute(() -> basePanel.entityDeleted(new TestEntity()));

        // verify
        String[] listContents = panelFixture.list("entityList").contents();
        assertThat(listContents).containsExactly(entity1.toString());
        verify(basePanel.getMainFrame()).resetErrorLabel();
    }

    @Test
    @GUITest
    public void testEntityUpdatedShouldUpdateEntityInListAndCallResetErrorLabelInParent() {
        // setup
        TestEntity entity = new TestEntity("field1");
        TestEntity entity2 = new TestEntity("field2");

        GuiActionRunner.execute(() -> {
            DefaultListModel<BaseEntity> listModel = basePanel.getListModel();
            listModel.addElement(entity);
            listModel.addElement(entity2);

        });
        entity2.setaField("field3");

        // exercise
        GuiActionRunner.execute(() -> basePanel.entityUpdated(entity2));

        // verify
        String[] listContents = panelFixture.list("entityList").contents();
        assertThat(listContents).containsExactly(entity.toString(),
                entity2.toString());
        verify(basePanel.getMainFrame()).resetErrorLabel();
    }

}
