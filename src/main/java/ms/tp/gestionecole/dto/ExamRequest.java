package ms.tp.gestionecole.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ExamRequest(
        @NotNull Long courseId,
        @NotBlank @Size(max = 150) String title,
        @NotNull LocalDateTime examDate,
        @Size(max = 100) String room) {
}
