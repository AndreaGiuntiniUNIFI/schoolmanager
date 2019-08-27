package apt.project.backend.domain;

import javax.persistence.Entity;

@Entity
public class TestEntity extends BaseEntity {
    // This entity has the only purpose to represent a concrete class
    // extending BaseEntity

    private String aField;

    public TestEntity() {
    }

    public String getaField() {
        return aField;
    }

    public void setaField(String aField) {
        this.aField = aField;
    }
}
