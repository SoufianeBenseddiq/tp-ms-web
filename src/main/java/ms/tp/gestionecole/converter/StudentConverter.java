package ms.tp.gestionecole.converter;

import org.springframework.stereotype.Component;

import ms.tp.gestionecole.dto.StudentRequest;
import ms.tp.gestionecole.dto.StudentResponse;
import ms.tp.gestionecole.entity.Student;

@Component
public class StudentConverter {

    public Student toEntity(StudentRequest req) {
        return new Student(req.firstName(), req.lastName(), req.email(), req.birthDate());
    }

    public void updateEntity(Student student, StudentRequest req) {
        student.setFirstName(req.firstName());
        student.setLastName(req.lastName());
        student.setEmail(req.email());
        student.setBirthDate(req.birthDate());
    }

    public StudentResponse toResponse(Student s) {
        return new StudentResponse(s.getId(), s.getFirstName(), s.getLastName(), s.getEmail(), s.getBirthDate());
    }
}
