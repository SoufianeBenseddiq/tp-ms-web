package ms.tp.gestionecole.service.facade;

import java.util.List;

import ms.tp.gestionecole.dto.RegistrationRequest;
import ms.tp.gestionecole.dto.RegistrationResponse;

public interface RegistrationService {

    RegistrationResponse register(RegistrationRequest req);

    RegistrationResponse findById(Long id);

    List<RegistrationResponse> search(Long studentId, Long courseId);
}
