package apt.project.frontend.controller;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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

    @Test
    public void testNewEntityWhenRepositoryExceptionIsThrownInSaveShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        TestEntity entity = new TestEntity();
        doThrow(new RepositoryException(message)).when(repository).save(entity);
        // exercise
        controller.newEntity(entity);
        // verify
        verify(view).showError("Repository exception: " + message, entity);
    }

    @Test
    public void testDeleteEntityWhenEntityExists() throws RepositoryException {
        // setup
        TestEntity entityToDelete = new TestEntity();
        when(repository.findById((Long) any())).thenReturn(entityToDelete);
        // exercise
        controller.deleteEntity(entityToDelete);
        // verify
        InOrder inOrder = inOrder(repository, view);
        inOrder.verify(repository).delete(entityToDelete);
        inOrder.verify(view).entityDeleted(entityToDelete);
    }

    @Test
    public void testDeleteEntityWhenEntityDoesNotExist()
            throws RepositoryException {
        // setup
        TestEntity entityToDelete = new TestEntity();
        when(repository.findById((Long) any())).thenReturn(null);
        // exercise
        controller.deleteEntity(entityToDelete);
        // verify
        verify(view).showError("No existing entity: " + entityToDelete,
                entityToDelete);
        verifyNoMoreInteractions(ignoreStubs(repository));
    }

    @Test
    public void testDeleteEntityWhenRepositoryExceptionIsThrownInFindByIdShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        TestEntity entityToDelete = new TestEntity();
        when(repository.findById((Long) any()))
                .thenThrow(new RepositoryException(message));
        // exercise
        controller.deleteEntity(entityToDelete);
        // verify
        verify(view).showError("Repository exception: " + message,
                entityToDelete);
        verifyNoMoreInteractions(ignoreStubs(repository));
    }

    @Test
    public void testDeleteEntityWhenRepositoryExceptionIsThrownInDeleteShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        TestEntity entityToDelete = new TestEntity();
        when(repository.findById((Long) any())).thenReturn(entityToDelete);
        doThrow(new RepositoryException(message)).when(repository)
                .delete(entityToDelete);
        // exercise
        controller.deleteEntity(entityToDelete);
        // verify
        verify(view).showError("Repository exception: " + message,
                entityToDelete);
        verifyNoMoreInteractions(ignoreStubs(repository));
    }

    @Test
    public void testUpdateEntityWhenEntityExists() throws RepositoryException {
        // setup
        TestEntity existingEntity = new TestEntity();
        TestEntity modifiedEntity = new TestEntity();
        when(repository.findById((Long) any())).thenReturn(existingEntity);
        // exercise
        controller.updateEntity(existingEntity, modifiedEntity);
        // verify
        InOrder inOrder = inOrder(repository, view);
        inOrder.verify(repository).update(modifiedEntity);
        inOrder.verify(view).entityUpdated(existingEntity, modifiedEntity);
    }

    @Test
    public void testUpdateEntityWhenEntityDoesNotExist()
            throws RepositoryException {
        // setup
        TestEntity existingEntity = new TestEntity();
        TestEntity modifiedEntity = new TestEntity();
        when(repository.findById((Long) any())).thenReturn(null);
        // exercise
        controller.updateEntity(existingEntity, modifiedEntity);
        // verify
        verify(view).showError("No existing entity: " + existingEntity,
                existingEntity);
        verifyNoMoreInteractions(ignoreStubs(repository));
    }

    @Test
    public void testUpdateEntityWhenRepositoryExceptionIsThrownInFindByIdShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        TestEntity existingEntity = new TestEntity();
        TestEntity modifiedEntity = new TestEntity();
        when(repository.findById((Long) any()))
                .thenThrow(new RepositoryException(message));
        // exercise
        controller.updateEntity(existingEntity, modifiedEntity);
        // verify
        verify(view).showError("Repository exception: " + message,
                existingEntity);
        verifyNoMoreInteractions(ignoreStubs(repository));
    }

    @Test
    public void testUpdateEntityWhenRepositoryExceptionIsThrownInUpdateShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        TestEntity existingEntity = new TestEntity();
        TestEntity modifiedEntity = new TestEntity();
        when(repository.findById((Long) any())).thenReturn(existingEntity);
        doThrow(new RepositoryException(message)).when(repository)
                .update(modifiedEntity);
        // exercise
        controller.updateEntity(existingEntity, modifiedEntity);
        // verify
        verify(view).showError("Repository exception: " + message,
                existingEntity);
        verifyNoMoreInteractions(ignoreStubs(repository));
    }

}
