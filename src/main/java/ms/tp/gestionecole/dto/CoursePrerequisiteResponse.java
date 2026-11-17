package ms.tp.gestionecole.dto;

public record CoursePrerequisiteResponse(Long id, Long courseId, String courseCode,
                                         Long prerequisiteId, String prerequisiteCode,
                                         Long equivalentId, String equivalentCode) {
}
