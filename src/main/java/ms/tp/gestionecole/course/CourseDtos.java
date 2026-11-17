package ms.tp.gestionecole.course;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class CourseDtos {

    private CourseDtos() {
    }

    public record CourseRequest(
            @NotBlank @Size(max = 20) String code,
            @NotBlank @Size(max = 150) String title,
            @NotNull @Min(1) Integer capacity) {
    }

    public record CourseResponse(Long id, String code, String title, int capacity,
                                 long enrolled, long availableSeats) {

        public static CourseResponse from(Course c, long enrolled) {
            return new CourseResponse(c.getId(), c.getCode(), c.getTitle(), c.getCapacity(),
                    enrolled, Math.max(0, c.getCapacity() - enrolled));
        }
    }
}
