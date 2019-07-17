package apt.project.frontend.view;

import java.util.List;

import apt.project.backend.domain.Entity;

public interface View {

    void showAll(List<Entity> courses);

    void entityAdded(Entity course);

    void showError(String string, Entity entity);

    void entityDeleted(Entity entity);

}
