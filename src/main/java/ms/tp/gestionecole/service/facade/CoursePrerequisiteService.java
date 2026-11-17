package ms.tp.gestionecole.service.facade;

import java.util.List;

import ms.tp.gestionecole.dto.CoursePrerequisiteRequest;
import ms.tp.gestionecole.dto.CoursePrerequisiteResponse;

public interface CoursePrerequisiteService {

    CoursePrerequisiteResponse add(Long courseId, CoursePrerequisiteRequest req);

    List<CoursePrerequisiteResponse> findByCourse(Long courseId);

    void delete(Long courseId, Long prerequisiteLinkId);
}
