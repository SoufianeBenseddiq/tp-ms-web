package ms.tp.gestionecole.service.facade;

import java.util.List;

import ms.tp.gestionecole.dto.GradeRequest;
import ms.tp.gestionecole.dto.GradeResponse;
import ms.tp.gestionecole.dto.GradeUpdateRequest;

public interface GradeService {

    GradeResponse record(GradeRequest req);

    GradeResponse update(Long id, GradeUpdateRequest req);

    GradeResponse findById(Long id);

    List<GradeResponse> search(Long examId, Long studentId);
}
