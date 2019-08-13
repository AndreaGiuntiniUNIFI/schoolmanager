package apt.project.backend.repository;

import javax.persistence.Entity;

import apt.project.backend.domain.BaseEntity;

@Entity
class TestEntity extends BaseEntity {
    // This entity has the only purpose to represent a concrete class
    // extending BaseEntity
    private String aField;

    public String getaField() {
        return aField;
    }

    public void setaField(String aField) {
        this.aField = aField;
    }
}