package apt.project.backend.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Exam extends BaseEntity {

    @ManyToOne
    private Course course;

    private Integer rate;

    public Exam() {

    }

    public Exam(Course course) {
        this.course = course;
    }

    public Exam(Course course, Integer rate) {
        this.course = course;
        this.rate = rate;
    }

    public void merge(Exam entity) {
        if (entity.getRate() != null) {
            this.rate = entity.getRate();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((course == null) ? 0 : course.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Exam other = (Exam) obj;
        if (course == null) {
            if (other.course != null)
                return false;
        } else if (!course.equals(other.course))
            return false;
        return true;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

}
