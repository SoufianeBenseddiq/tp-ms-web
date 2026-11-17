package ms.tp.gestionecole.dto;

import jakarta.validation.constraints.NotNull;

public record RegistrationRequest(@NotNull Long studentId, @NotNull Long courseId) {
}
