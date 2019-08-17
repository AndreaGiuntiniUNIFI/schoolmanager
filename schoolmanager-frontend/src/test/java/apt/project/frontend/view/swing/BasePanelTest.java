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
import apt.project.frontend.view.MainFrame;

public class BasePanelTest extends AssertJSwingJUnitTestCase {

    private static final String HEADER_TEXT = "Base panel";

    private BasePanel<BaseEntity> basePanel;

    private JPanel internalPanel;
    private JPanelFixture panelFixture;
    private JFrame frame;
    private MainFrame mainFrame;

    private class TestEntity extends BaseEntity {
        // This entity has the only purpose to represent a concrete class
        // extending BaseEntity.
        private int aField;

        public TestEntity() {
        }

        public TestEntity(int field) {
            setaField(field);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + getEnclosingInstance().hashCode();
            result = prime * result + aField;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!super.equals(obj))
                return false;
            if (getClass() != obj.getClass())
                return false;
            TestEntity other = (TestEntity) obj;
            if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
                return false;
            if (aField != other.aField)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "TestEntity [aField=" + getaField() + "]";
        }

        public int getaField() {
            return aField;
        }

        public void setaField(int aField) {
            this.aField = aField;
        }

        private BasePanelTest getEnclosingInstance() {
            return BasePanelTest.this;
        }

    }

    @Override
    protected void onSetUp() {

        DialogManager dialogManager = mock(DialogManager.class);
        mainFrame = mock(MainFrame.class);

        GuiActionRunner.execute(() -> {
            internalPanel = new JPanel();
            basePanel = new BasePanel<>(internalPanel, mainFrame, dialogManager,
                    HEADER_TEXT);
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
        // setup
        TestEntity entity = new TestEntity();

        // exercise
        GuiActionRunner
                .execute(() -> basePanel.showError("error message", entity));

        // verify
        verify(basePanel.getParentMainFrame())
                .displayErrorLabel("error message: " + entity);
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
        verify(basePanel.getParentMainFrame()).resetErrorLabel();
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
        verify(basePanel.getParentMainFrame()).resetErrorLabel();
    }

    @Test
    @GUITest
    public void testEntityUpdatedShouldUpdateEntityInListAndCallResetErrorLabelInParent() {
        // setup
        TestEntity entity = new TestEntity(1);
        TestEntity entity2 = new TestEntity(4);

        GuiActionRunner.execute(() -> {
            DefaultListModel<BaseEntity> listModel = basePanel.getListModel();
            listModel.addElement(entity);
            listModel.addElement(entity2);

        });
        entity2.setaField(2);

        System.out.println(basePanel.getListModel().indexOf(entity2));

        // exercise

        GuiActionRunner.execute(() -> basePanel.entityUpdated(entity2));

        // verify
        String[] listContents = panelFixture.list("entityList").contents();
        assertThat(listContents).containsExactly(entity.toString(),
                entity2.toString());
        verify(basePanel.getParentMainFrame()).resetErrorLabel();
    }

}
