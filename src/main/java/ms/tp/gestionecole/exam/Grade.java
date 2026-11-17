package ms.tp.gestionecole.exam;

import java.math.BigDecimal;
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
import jakarta.persistence.UniqueConstraint;
import ms.tp.gestionecole.student.Student;

/**
 * Note (sur 20) obtenue par un étudiant à un examen.
 */
@Entity
@Table(name = "grades",
        uniqueConstraints = @UniqueConstraint(name = "uk_grade_exam_student",
                columnNames = {"exam_id", "student_id"}))
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal score;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    protected Grade() {
    }

    public Grade(Exam exam, Student student, BigDecimal score) {
        this.exam = exam;
        this.student = student;
        setScore(score);
    }

    public Long getId() { return id; }
    public Exam getExam() { return exam; }
    public Student getStudent() { return student; }
    public BigDecimal getScore() { return score; }
    public LocalDateTime getRecordedAt() { return recordedAt; }

    public void setScore(BigDecimal score) {
        this.score = score;
        this.recordedAt = LocalDateTime.now();
    }
}
