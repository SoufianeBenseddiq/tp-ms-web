package ms.tp.gestionecole.repository;

import ms.tp.gestionecole.entity.Exam;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    @Override
    @EntityGraph(attributePaths = "course")
    List<Exam> findAll();

    @EntityGraph(attributePaths = "course")
    List<Exam> findByCourseIdOrderByExamDateAsc(Long courseId);
}
