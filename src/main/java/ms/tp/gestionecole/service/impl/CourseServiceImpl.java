package ms.tp.gestionecole.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ms.tp.gestionecole.converter.CourseConverter;
import ms.tp.gestionecole.dto.CourseRequest;
import ms.tp.gestionecole.dto.CourseResponse;
import ms.tp.gestionecole.entity.Course;
import ms.tp.gestionecole.exception.BusinessException;
import ms.tp.gestionecole.exception.ResourceNotFoundException;
import ms.tp.gestionecole.repository.CourseRepository;
import ms.tp.gestionecole.repository.RegistrationRepository;
import ms.tp.gestionecole.service.facade.CourseService;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository repository;
    private final RegistrationRepository registrationRepository;
    private final CourseConverter converter;

    public CourseServiceImpl(CourseRepository repository, RegistrationRepository registrationRepository,
                             CourseConverter converter) {
        this.repository = repository;
        this.registrationRepository = registrationRepository;
        this.converter = converter;
    }

    @Override
    public CourseResponse create(CourseRequest req) {
        if (repository.existsByCodeIgnoreCase(req.code())) {
            throw new BusinessException("Un module avec le code " + req.code() + " existe déjà");
        }
        return converter.toResponse(repository.save(converter.toEntity(req)), 0);
    }

    @Override
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
        converter.updateEntity(c, req);
        return converter.toResponse(c, enrolled);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse findById(Long id) {
        Course c = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Module", id));
        return converter.toResponse(c, registrationRepository.countByCourseId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> findAll() {
        return repository.findAll().stream()
                .map(c -> converter.toResponse(c, registrationRepository.countByCourseId(c.getId())))
                .toList();
    }
}
