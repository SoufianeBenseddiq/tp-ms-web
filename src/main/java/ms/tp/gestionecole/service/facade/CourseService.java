package ms.tp.gestionecole.service.facade;

import java.util.List;

import ms.tp.gestionecole.dto.CourseRequest;
import ms.tp.gestionecole.dto.CourseResponse;

public interface CourseService {

    CourseResponse create(CourseRequest req);

    CourseResponse update(Long id, CourseRequest req);

    CourseResponse findById(Long id);

    List<CourseResponse> findAll();
}
