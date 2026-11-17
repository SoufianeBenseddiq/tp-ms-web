package ms.tp.gestionecole.exam;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    boolean existsByExamIdAndStudentId(Long examId, Long studentId);

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
