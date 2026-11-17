package ms.tp.gestionecole.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import ms.tp.gestionecole.entity.CoursePrerequisite;

public interface CoursePrerequisiteRepository extends JpaRepository<CoursePrerequisite, Long> {

    @EntityGraph(attributePaths = {"course", "prerequisite", "equivalent"})
    List<CoursePrerequisite> findByCourseId(Long courseId);

    @EntityGraph(attributePaths = {"course", "prerequisite", "equivalent"})
    Optional<CoursePrerequisite> findByIdAndCourseId(Long id, Long courseId);

    boolean existsByCourseIdAndPrerequisiteId(Long courseId, Long prerequisiteId);
}
