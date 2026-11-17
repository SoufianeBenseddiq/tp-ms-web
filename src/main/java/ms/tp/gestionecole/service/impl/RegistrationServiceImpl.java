package ms.tp.gestionecole.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ms.tp.gestionecole.converter.RegistrationConverter;
import ms.tp.gestionecole.dto.RegistrationRequest;
import ms.tp.gestionecole.dto.RegistrationResponse;
import ms.tp.gestionecole.entity.Course;
import ms.tp.gestionecole.entity.Registration;
import ms.tp.gestionecole.entity.Student;
import ms.tp.gestionecole.exception.BusinessException;
import ms.tp.gestionecole.exception.ResourceNotFoundException;
import ms.tp.gestionecole.repository.CourseRepository;
import ms.tp.gestionecole.repository.RegistrationRepository;
import ms.tp.gestionecole.repository.StudentRepository;
import ms.tp.gestionecole.service.facade.RegistrationService;

@Service
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository repository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final RegistrationConverter converter;

    public RegistrationServiceImpl(RegistrationRepository repository, StudentRepository studentRepository,
                                   CourseRepository courseRepository, RegistrationConverter converter) {
        this.repository = repository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.converter = converter;
    }

    @Override
    public RegistrationResponse register(RegistrationRequest req) {
        Student student = studentRepository.findById(req.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant", req.studentId()));
        // Verrou sur le module : deux inscriptions simultanées ne peuvent pas dépasser la capacité
        Course course = courseRepository.findByIdForUpdate(req.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Module", req.courseId()));

        if (repository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new BusinessException("L'étudiant " + student.getId() + " est déjà inscrit au module " + course.getCode());
        }
        long enrolled = repository.countByCourseId(course.getId());
        if (enrolled >= course.getCapacity()) {
            throw new BusinessException("Le module " + course.getCode() + " est complet (capacité " + course.getCapacity() + ")");
        }
        return converter.toResponse(repository.save(new Registration(student, course)));
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationResponse findById(Long id) {
        return converter.toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationResponse> search(Long studentId, Long courseId) {
        List<Registration> result;
        if (studentId != null && courseId != null) {
            result = repository.findByStudentIdAndCourseId(studentId, courseId);
        } else if (studentId != null) {
            result = repository.findByStudentId(studentId);
        } else if (courseId != null) {
            result = repository.findByCourseId(courseId);
        } else {
            result = repository.findAll();
        }
        return result.stream().map(converter::toResponse).toList();
    }
}
