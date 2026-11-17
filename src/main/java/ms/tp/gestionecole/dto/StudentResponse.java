package ms.tp.gestionecole.dto;

import java.time.LocalDate;

public record StudentResponse(Long id, String firstName, String lastName, String email, LocalDate birthDate) {
}
