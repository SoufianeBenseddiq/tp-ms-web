package ms.tp.gestionecole.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Prérequis d'un module : pour s'inscrire à {@code course}, l'étudiant doit avoir validé
 * {@code prerequisite} (moyenne >= 10) ou, à défaut, le module {@code equivalent} s'il est défini.
 */
@Entity
@Table(name = "course_prerequisites",
        uniqueConstraints = @UniqueConstraint(name = "uk_course_prerequisite",
                columnNames = {"course_id", "prerequisite_id"}))
public class CoursePrerequisite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prerequisite_id", nullable = false)
    private Course prerequisite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equivalent_id")
    private Course equivalent;

    protected CoursePrerequisite() {
    }

    public CoursePrerequisite(Course course, Course prerequisite, Course equivalent) {
        this.course = course;
        this.prerequisite = prerequisite;
        this.equivalent = equivalent;
    }

    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public Course getPrerequisite() { return prerequisite; }
    public Course getEquivalent() { return equivalent; }
}
