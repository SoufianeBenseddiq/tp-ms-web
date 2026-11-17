package ms.tp.gestionecole.student;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ms.tp.gestionecole.common.BusinessException;
import ms.tp.gestionecole.common.ResourceNotFoundException;
import ms.tp.gestionecole.student.StudentDtos.StudentRequest;
import ms.tp.gestionecole.student.StudentDtos.StudentResponse;

@Service
@Transactional
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public StudentResponse create(StudentRequest req) {
        if (repository.existsByEmailIgnoreCase(req.email())) {
            throw new BusinessException("Un étudiant avec l'email " + req.email() + " existe déjà");
        }
        Student s = new Student(req.firstName(), req.lastName(), req.email(), req.birthDate());
        return StudentResponse.from(repository.save(s));
    }

    public StudentResponse update(Long id, StudentRequest req) {
        Student s = getEntity(id);
        if (repository.existsByEmailIgnoreCaseAndIdNot(req.email(), id)) {
            throw new BusinessException("Un étudiant avec l'email " + req.email() + " existe déjà");
        }
        s.setFirstName(req.firstName());
        s.setLastName(req.lastName());
        s.setEmail(req.email());
        s.setBirthDate(req.birthDate());
        return StudentResponse.from(s);
    }

    @Transactional(readOnly = true)
    public StudentResponse findById(Long id) {
        return StudentResponse.from(getEntity(id));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> findAll() {
        return repository.findAll().stream().map(StudentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Student getEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Étudiant", id));
    }
}
