package ms.tp.gestionecole.service.facade;

import java.util.List;

import ms.tp.gestionecole.dto.ExamRequest;
import ms.tp.gestionecole.dto.ExamResponse;

public interface ExamService {

    ExamResponse planExam(ExamRequest req);

    ExamResponse findById(Long id);

    List<ExamResponse> findAll(Long courseId);
}
