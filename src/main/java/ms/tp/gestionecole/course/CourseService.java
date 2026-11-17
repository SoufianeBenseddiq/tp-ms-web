package ms.tp.gestionecole.course;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ms.tp.gestionecole.common.BusinessException;
import ms.tp.gestionecole.common.ResourceNotFoundException;
import ms.tp.gestionecole.course.CourseDtos.CourseRequest;
import ms.tp.gestionecole.course.CourseDtos.CourseResponse;
import ms.tp.gestionecole.registration.RegistrationRepository;

@Service
@Transactional
public class CourseService {

    private final CourseRepository repository;
    private final RegistrationRepository registrationRepository;

    public CourseService(CourseRepository repository, RegistrationRepository registrationRepository) {
        this.repository = repository;
        this.registrationRepository = registrationRepository;
    }

    public CourseResponse create(CourseRequest req) {
        if (repository.existsByCodeIgnoreCase(req.code())) {
            throw new BusinessException("Un module avec le code " + req.code() + " existe déjà");
        }
        Course c = repository.save(new Course(req.code(), req.title(), req.capacity()));
        return CourseResponse.from(c, 0);
    }

    public CourseResponse update(Long id, CourseRequest req) {
        Course c = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module", id));
        if (repository.existsByCodeIgnoreCaseAndIdNot(req.code(), id)) {
            throw new BusinessException("Un module avec le code " + req.code() + " existe déjà");
        }
        long enrolled = registrationRepository.countByCourseId(id);
        if (req.capacity() < enrolled) {
            throw new BusinessException("Capacité (" + req.capacity() + ") inférieure au nombre d'inscrits (" + enrolled + ")");
        }
        c.setCode(req.code());
        c.setTitle(req.title());
        c.setCapacity(req.capacity());
        return CourseResponse.from(c, enrolled);
    }

    @Transactional(readOnly = true)
    public CourseResponse findById(Long id) {
        return CourseResponse.from(getEntity(id), registrationRepository.countByCourseId(id));
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> findAll() {
        return repository.findAll().stream()
                .map(c -> CourseResponse.from(c, registrationRepository.countByCourseId(c.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public Course getEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Module", id));
    }

    public Course getEntityForUpdate(Long id) {
        return repository.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Module", id));
    }
}
