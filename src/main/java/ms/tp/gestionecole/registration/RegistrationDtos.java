package ms.tp.gestionecole.registration;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public final class RegistrationDtos {

    private RegistrationDtos() {
    }

    public record RegistrationRequest(@NotNull Long studentId, @NotNull Long courseId) {
    }

    public record RegistrationResponse(Long id, Long studentId, String studentName,
                                       Long courseId, String courseCode, LocalDateTime registeredAt) {

        public static RegistrationResponse from(Registration r) {
            return new RegistrationResponse(r.getId(),
                    r.getStudent().getId(),
                    r.getStudent().getFirstName() + " " + r.getStudent().getLastName(),
                    r.getCourse().getId(),
                    r.getCourse().getCode(),
                    r.getRegisteredAt());
        }
    }
}
