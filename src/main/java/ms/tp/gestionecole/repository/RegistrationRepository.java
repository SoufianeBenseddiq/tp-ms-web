package ms.tp.gestionecole.repository;

import ms.tp.gestionecole.entity.Registration;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    long countByCourseId(Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    @Override
    @EntityGraph(attributePaths = {"student", "course"})
    List<Registration> findAll();

    @EntityGraph(attributePaths = {"student", "course"})
    List<Registration> findByStudentId(Long studentId);

    @EntityGraph(attributePaths = {"student", "course"})
    List<Registration> findByCourseId(Long courseId);

    @EntityGraph(attributePaths = {"student", "course"})
    List<Registration> findByStudentIdAndCourseId(Long studentId, Long courseId);
}
