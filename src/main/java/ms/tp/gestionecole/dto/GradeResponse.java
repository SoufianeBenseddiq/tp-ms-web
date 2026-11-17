package ms.tp.gestionecole.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GradeResponse(Long id, Long examId, String examTitle, String courseCode,
                            Long studentId, String studentName, BigDecimal score, LocalDateTime recordedAt) {
}
