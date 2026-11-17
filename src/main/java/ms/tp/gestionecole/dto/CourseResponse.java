package ms.tp.gestionecole.dto;

public record CourseResponse(Long id, String code, String title, int capacity,
                             long enrolled, long availableSeats) {
}
