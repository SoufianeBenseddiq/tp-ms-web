package ms.tp.gestionecole.exam;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ms.tp.gestionecole.course.Course;

/**
 * Examen planifié pour un module.
 */
@Entity
@Table(name = "exams")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false)
    private LocalDateTime examDate;

    @Column(length = 100)
    private String room;

    protected Exam() {
    }

    public Exam(Course course, String title, LocalDateTime examDate, String room) {
        this.course = course;
        this.title = title;
        this.examDate = examDate;
        this.room = room;
    }

    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public String getTitle() { return title; }
    public LocalDateTime getExamDate() { return examDate; }
    public String getRoom() { return room; }
}
