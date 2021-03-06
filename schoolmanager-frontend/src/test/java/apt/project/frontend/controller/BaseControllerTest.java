package apt.project.frontend.controller;

import static java.util.Arrays.asList;
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
import apt.project.frontend.testdomain.TestEntity;
import apt.project.frontend.view.View;

public class BaseControllerTest {

    @Mock
    private Repository<BaseEntity> repository;

    @Mock
    private View<BaseEntity> view;

    @InjectMocks
    private BaseController<BaseEntity> controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAllEntities() throws RepositoryException {
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
        verify(view).showError("Repository exception: message, " + null);
        verifyNoMoreInteractions(view);
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
        verify(view).showError("Repository exception: message, " + entity);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testDeleteEntityWhenEntityExists() throws RepositoryException {
        // setup
        TestEntity entityToDelete = new TestEntity("field", 1L);
        when(repository.findById(1L)).thenReturn(entityToDelete);
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
        TestEntity entityToDelete = new TestEntity("field", 1L);
        when(repository.findById(1L)).thenReturn(null);
        // exercise
        controller.deleteEntity(entityToDelete);
        // verify
        verify(view).showError("No existing entity: " + entityToDelete);
        verifyNoMoreInteractions(ignoreStubs(repository));
    }

    @Test
    public void testDeleteEntityWhenRepositoryExceptionIsThrownInFindByIdShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        TestEntity entityToDelete = new TestEntity("field", 1L);
        when(repository.findById(1L))
                .thenThrow(new RepositoryException(message));
        // exercise
        controller.deleteEntity(entityToDelete);
        // verify
        verify(view)
                .showError("Repository exception: message, " + entityToDelete);
        verifyNoMoreInteractions(ignoreStubs(repository));
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testDeleteEntityWhenRepositoryExceptionIsThrownInDeleteShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        TestEntity entityToDelete = new TestEntity("field", 1L);
        when(repository.findById(1L)).thenReturn(entityToDelete);
        doThrow(new RepositoryException(message)).when(repository)
                .delete(entityToDelete);
        // exercise
        controller.deleteEntity(entityToDelete);
        // verify
        verify(view)
                .showError("Repository exception: message, " + entityToDelete);
        verifyNoMoreInteractions(ignoreStubs(repository));
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testUpdateEntityWhenEntityExists() throws RepositoryException {
        // setup
        TestEntity existingEntity = new TestEntity("field1", 1L);
        TestEntity modifiedEntity = new TestEntity("field2", 1L);
        when(repository.findById(1L)).thenReturn(existingEntity);
        // exercise
        controller.updateEntity(modifiedEntity);
        // verify
        InOrder inOrder = inOrder(repository, view);
        inOrder.verify(repository).update(modifiedEntity);
        inOrder.verify(view).entityUpdated(modifiedEntity);
    }

    @Test
    public void testUpdateEntityWhenEntityDoesNotExist()
            throws RepositoryException {
        // setup
        TestEntity modifiedEntity = new TestEntity("field", 1L);
        when(repository.findById(1L)).thenReturn(null);
        // exercise
        controller.updateEntity(modifiedEntity);
        // verify
        verify(view).showError("No existing entity: " + modifiedEntity);
        verifyNoMoreInteractions(ignoreStubs(repository));
    }

    @Test
    public void testUpdateEntityWhenRepositoryExceptionIsThrownInFindByIdShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        TestEntity modifiedEntity = new TestEntity("field", 1L);
        when(repository.findById(1L))
                .thenThrow(new RepositoryException(message));
        // exercise
        controller.updateEntity(modifiedEntity);
        // verify
        verify(view)
                .showError("Repository exception: message, " + modifiedEntity);
        verifyNoMoreInteractions(ignoreStubs(repository));
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testUpdateEntityWhenRepositoryExceptionIsThrownInUpdateShouldCallShowError()
            throws RepositoryException {
        // setup
        String message = "message";
        TestEntity existingEntity = new TestEntity("field1", 1L);
        TestEntity modifiedEntity = new TestEntity("field2", 1L);
        when(repository.findById(1L)).thenReturn(existingEntity);
        doThrow(new RepositoryException(message)).when(repository)
                .update(modifiedEntity);
        // exercise
        controller.updateEntity(modifiedEntity);
        // verify
        verify(view)
                .showError("Repository exception: message, " + modifiedEntity);
        verifyNoMoreInteractions(ignoreStubs(repository));
        verifyNoMoreInteractions(view);
    }

}
