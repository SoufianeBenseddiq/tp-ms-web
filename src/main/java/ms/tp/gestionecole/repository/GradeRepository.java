package ms.tp.gestionecole.repository;

import ms.tp.gestionecole.entity.Grade;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    boolean existsByExamIdAndStudentId(Long examId, Long studentId);

    /** Moyenne des notes d'un étudiant aux examens d'un module ; null s'il n'a aucune note. */
    @Query("select avg(g.score) from Grade g where g.student.id = :studentId and g.exam.course.id = :courseId")
    Double averageScore(Long studentId, Long courseId);

    @Override
    @EntityGraph(attributePaths = {"exam", "exam.course", "student"})
    List<Grade> findAll();

    @EntityGraph(attributePaths = {"exam", "exam.course", "student"})
    List<Grade> findByExamId(Long examId);

    @EntityGraph(attributePaths = {"exam", "exam.course", "student"})
    List<Grade> findByStudentId(Long studentId);

    @EntityGraph(attributePaths = {"exam", "exam.course", "student"})
    List<Grade> findByExamIdAndStudentId(Long examId, Long studentId);
}
