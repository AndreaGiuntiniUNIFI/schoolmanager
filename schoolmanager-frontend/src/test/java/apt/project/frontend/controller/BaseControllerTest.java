package apt.project.frontend.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import apt.project.backend.domain.BaseEntity;
import apt.project.backend.repository.Repository;
import apt.project.backend.repository.RepositoryException;
import apt.project.frontend.view.View;

public class BaseControllerTest {

    @Mock
    private Repository<BaseEntity> repository;

    @Mock
    private View<BaseEntity> view;

    @InjectMocks
    private BaseController<BaseEntity> controller;

    private class TestEntity extends BaseEntity {
        // This entity has the only purpose to represent a concrete class
        // extending BaseEntity
    };

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAllEntitiesShouldCallView() throws RepositoryException {
        // setup
        List<BaseEntity> entities = asList(new TestEntity());
        when(repository.findAll()).thenReturn(entities);
        // exercise
        controller.allEntities();
        // verify
        verify(view).showAll(entities);
    }

    @Test
    public void testAllEntitiesWhenRepositoryExceptionIsThrownInFindAllShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        when(repository.findAll()).thenThrow(new RepositoryException(message));
        // exercise
        controller.allEntities();
        // verify
        verify(view).showError("Repository exception: " + message, null);
    }

    @Test
    public void testNewEntity() throws RepositoryException {
        // setup
        TestEntity entity = new TestEntity();
        // exercise
        controller.newEntity(entity);
        // verify
        InOrder inOrder = inOrder(repository, view);
        inOrder.verify(repository).save(entity);
        inOrder.verify(view).entityAdded(entity);
    }

}
