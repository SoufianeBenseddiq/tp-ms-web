package ms.tp.gestionecole.dto;

import java.time.LocalDateTime;

public record RegistrationResponse(Long id, Long studentId, String studentName,
                                   Long courseId, String courseCode, LocalDateTime registeredAt) {
}
