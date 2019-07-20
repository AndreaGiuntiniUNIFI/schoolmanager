package apt.project.backend.domain;

import javax.persistence.Entity;

@Entity
public class Course extends BaseEntity {

    private String title;

    public Course() {}
    
    public Course(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
