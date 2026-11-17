package ms.tp.gestionecole.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ms.tp.gestionecole.converter.StudentConverter;
import ms.tp.gestionecole.dto.StudentRequest;
import ms.tp.gestionecole.dto.StudentResponse;
import ms.tp.gestionecole.entity.Student;
import ms.tp.gestionecole.exception.BusinessException;
import ms.tp.gestionecole.exception.ResourceNotFoundException;
import ms.tp.gestionecole.repository.StudentRepository;
import ms.tp.gestionecole.service.facade.StudentService;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final StudentConverter converter;

    public StudentServiceImpl(StudentRepository repository, StudentConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public StudentResponse create(StudentRequest req) {
        if (repository.existsByEmailIgnoreCase(req.email())) {
            throw new BusinessException("Un étudiant avec l'email " + req.email() + " existe déjà");
        }
        return converter.toResponse(repository.save(converter.toEntity(req)));
    }

    @Override
    public StudentResponse update(Long id, StudentRequest req) {
        Student s = getEntity(id);
        if (repository.existsByEmailIgnoreCaseAndIdNot(req.email(), id)) {
            throw new BusinessException("Un étudiant avec l'email " + req.email() + " existe déjà");
        }
        converter.updateEntity(s, req);
        return converter.toResponse(s);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse findById(Long id) {
        return converter.toResponse(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> findAll() {
        return repository.findAll().stream().map(converter::toResponse).toList();
    }

    private Student getEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Étudiant", id));
    }
}
