package ms.tp.gestionecole.converter;

import org.springframework.stereotype.Component;

import ms.tp.gestionecole.dto.RegistrationResponse;
import ms.tp.gestionecole.entity.Registration;

@Component
public class RegistrationConverter {

    public RegistrationResponse toResponse(Registration r) {
        return new RegistrationResponse(r.getId(),
                r.getStudent().getId(),
                r.getStudent().getFirstName() + " " + r.getStudent().getLastName(),
                r.getCourse().getId(),
                r.getCourse().getCode(),
                r.getRegisteredAt());
    }
}
