package ms.tp.gestionecole.registration;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ms.tp.gestionecole.common.BusinessException;
import ms.tp.gestionecole.common.ResourceNotFoundException;
import ms.tp.gestionecole.course.Course;
import ms.tp.gestionecole.course.CourseService;
import ms.tp.gestionecole.registration.RegistrationDtos.RegistrationRequest;
import ms.tp.gestionecole.registration.RegistrationDtos.RegistrationResponse;
import ms.tp.gestionecole.student.Student;
import ms.tp.gestionecole.student.StudentService;

@Service
@Transactional
public class RegistrationService {

    private final RegistrationRepository repository;
    private final StudentService studentService;
    private final CourseService courseService;

    public RegistrationService(RegistrationRepository repository, StudentService studentService,
                               CourseService courseService) {
        this.repository = repository;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    public RegistrationResponse register(RegistrationRequest req) {
        Student student = studentService.getEntity(req.studentId());
        // Verrou sur le module : deux inscriptions simultanées ne peuvent pas dépasser la capacité
        Course course = courseService.getEntityForUpdate(req.courseId());

        if (repository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new BusinessException("L'étudiant " + student.getId() + " est déjà inscrit au module " + course.getCode());
        }
        long enrolled = repository.countByCourseId(course.getId());
        if (enrolled >= course.getCapacity()) {
            throw new BusinessException("Le module " + course.getCode() + " est complet (capacité " + course.getCapacity() + ")");
        }
        return RegistrationResponse.from(repository.save(new Registration(student, course)));
    }

    @Transactional(readOnly = true)
    public RegistrationResponse findById(Long id) {
        return RegistrationResponse.from(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription", id)));
    }

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
        return result.stream().map(RegistrationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public boolean isRegistered(Long studentId, Long courseId) {
        return repository.existsByStudentIdAndCourseId(studentId, courseId);
    }
}
