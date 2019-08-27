package apt.project.frontend.testdomain;

import javax.persistence.Entity;

import apt.project.backend.domain.BaseEntity;

@Entity
public class TestEntity extends BaseEntity {
    // This entity has the only purpose to represent a concrete class
    // extending BaseEntity

    private String aField;

    public TestEntity() {
    }

    public TestEntity(String field, Long id) {
        aField = field;
        setId(id);
    }

    public TestEntity(String field) {
        aField = field;
    }

    public String getaField() {
        return aField;
    }

    public void setaField(String aField) {
        this.aField = aField;
    }

    @Override
    public String toString() {
        return "TestEntity [aField=" + getaField() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((aField == null) ? 0 : aField.hashCode());
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
        if (aField == null) {
            if (other.aField != null)
                return false;
        } else if (!aField.equals(other.aField))
            return false;
        return true;
    }

}
