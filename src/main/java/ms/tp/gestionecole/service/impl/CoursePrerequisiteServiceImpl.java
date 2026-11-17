package ms.tp.gestionecole.service.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ms.tp.gestionecole.converter.CoursePrerequisiteConverter;
import ms.tp.gestionecole.dto.CoursePrerequisiteRequest;
import ms.tp.gestionecole.dto.CoursePrerequisiteResponse;
import ms.tp.gestionecole.entity.Course;
import ms.tp.gestionecole.entity.CoursePrerequisite;
import ms.tp.gestionecole.exception.BusinessException;
import ms.tp.gestionecole.exception.ResourceNotFoundException;
import ms.tp.gestionecole.repository.CoursePrerequisiteRepository;
import ms.tp.gestionecole.repository.CourseRepository;
import ms.tp.gestionecole.service.facade.CoursePrerequisiteService;

@Service
@Transactional
public class CoursePrerequisiteServiceImpl implements CoursePrerequisiteService {

    private final CoursePrerequisiteRepository repository;
    private final CourseRepository courseRepository;
    private final CoursePrerequisiteConverter converter;

    public CoursePrerequisiteServiceImpl(CoursePrerequisiteRepository repository, CourseRepository courseRepository,
                                         CoursePrerequisiteConverter converter) {
        this.repository = repository;
        this.courseRepository = courseRepository;
        this.converter = converter;
    }

    @Override
    public CoursePrerequisiteResponse add(Long courseId, CoursePrerequisiteRequest req) {
        Course course = getCourse(courseId);
        Course prerequisite = getCourse(req.prerequisiteId());
        Course equivalent = req.equivalentId() != null ? getCourse(req.equivalentId()) : null;

        if (prerequisite.getId().equals(course.getId())) {
            throw new BusinessException("Un module ne peut pas être son propre prérequis");
        }
        if (equivalent != null) {
            if (equivalent.getId().equals(course.getId())) {
                throw new BusinessException("Un module ne peut pas être son propre équivalent de prérequis");
            }
            if (equivalent.getId().equals(prerequisite.getId())) {
                throw new BusinessException("Le module équivalent doit être différent du prérequis");
            }
        }
        if (repository.existsByCourseIdAndPrerequisiteId(course.getId(), prerequisite.getId())) {
            throw new BusinessException("Le module " + prerequisite.getCode() + " est déjà prérequis de " + course.getCode());
        }
        checkNoCycle(course, prerequisite);
        if (equivalent != null) {
            checkNoCycle(course, equivalent);
        }
        return converter.toResponse(repository.save(converter.toEntity(course, prerequisite, equivalent)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoursePrerequisiteResponse> findByCourse(Long courseId) {
        getCourse(courseId);
        return repository.findByCourseId(courseId).stream().map(converter::toResponse).toList();
    }

    @Override
    public void delete(Long courseId, Long prerequisiteLinkId) {
        CoursePrerequisite link = repository.findByIdAndCourseId(prerequisiteLinkId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Prérequis", prerequisiteLinkId));
        repository.delete(link);
    }

    /**
     * Ajouter "course exige required" crée un cycle si "required" exige déjà (directement ou
     * indirectement, via prérequis ou équivalents) le module "course". Parcours en largeur (BFS).
     */
    private void checkNoCycle(Course course, Course required) {
        Set<Long> visited = new HashSet<>();
        Deque<Long> queue = new ArrayDeque<>();
        queue.add(required.getId());
        while (!queue.isEmpty()) {
            Long current = queue.poll();
            if (current.equals(course.getId())) {
                throw new BusinessException("Ajout impossible : " + required.getCode()
                        + " dépend déjà de " + course.getCode() + " (cycle de prérequis)");
            }
            if (!visited.add(current)) {
                continue;
            }
            for (CoursePrerequisite p : repository.findByCourseId(current)) {
                queue.add(p.getPrerequisite().getId());
                if (p.getEquivalent() != null) {
                    queue.add(p.getEquivalent().getId());
                }
            }
        }
    }

    private Course getCourse(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Module", id));
    }
}
