package ms.tp.gestionecole.service.facade;

import java.util.List;

import ms.tp.gestionecole.dto.StudentRequest;
import ms.tp.gestionecole.dto.StudentResponse;

public interface StudentService {

    StudentResponse create(StudentRequest req);

    StudentResponse update(Long id, StudentRequest req);

    StudentResponse findById(Long id);

    List<StudentResponse> findAll();
}
