package apt.project.backend.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Exam extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
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
        int result = super.hashCode();
        result = prime * result + ((course == null) ? 0 : course.hashCode());
        result = prime * result + ((rate == null) ? 0 : rate.hashCode());
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
        Exam other = (Exam) obj;
        if (course == null) {
            if (other.course != null)
                return false;
        } else if (!course.equals(other.course))
            return false;
        if (rate == null) {
            if (other.rate != null)
                return false;
        } else if (!rate.equals(other.rate))
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

    @Override
    public String toString() {
        return "Exam [course=" + course + ", rate=" + rate + "]";
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

}
