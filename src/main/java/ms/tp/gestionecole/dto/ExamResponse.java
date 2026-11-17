package ms.tp.gestionecole.dto;

import java.time.LocalDateTime;

public record ExamResponse(Long id, Long courseId, String courseCode, String title,
                           LocalDateTime examDate, String room) {
}
