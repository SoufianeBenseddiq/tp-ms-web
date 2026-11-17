package ms.tp.gestionecole.exam;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class ExamDtos {

    private ExamDtos() {
    }

    public record ExamRequest(
            @NotNull Long courseId,
            @NotBlank @Size(max = 150) String title,
            @NotNull LocalDateTime examDate,
            @Size(max = 100) String room) {
    }

    public record ExamResponse(Long id, Long courseId, String courseCode, String title,
                               LocalDateTime examDate, String room) {

        public static ExamResponse from(Exam e) {
            return new ExamResponse(e.getId(), e.getCourse().getId(), e.getCourse().getCode(),
                    e.getTitle(), e.getExamDate(), e.getRoom());
        }
    }

    public record GradeRequest(
            @NotNull Long examId,
            @NotNull Long studentId,
            @NotNull @DecimalMin("0.00") @DecimalMax("20.00") @Digits(integer = 2, fraction = 2) BigDecimal score) {
    }

    public record GradeUpdateRequest(
            @NotNull @DecimalMin("0.00") @DecimalMax("20.00") @Digits(integer = 2, fraction = 2) BigDecimal score) {
    }

    public record GradeResponse(Long id, Long examId, String examTitle, String courseCode,
                                Long studentId, String studentName, BigDecimal score, LocalDateTime recordedAt) {

        public static GradeResponse from(Grade g) {
            return new GradeResponse(g.getId(),
                    g.getExam().getId(), g.getExam().getTitle(), g.getExam().getCourse().getCode(),
                    g.getStudent().getId(),
                    g.getStudent().getFirstName() + " " + g.getStudent().getLastName(),
                    g.getScore(), g.getRecordedAt());
        }
    }
}
