package ms.tp.gestionecole.converter;

import org.springframework.stereotype.Component;

import ms.tp.gestionecole.dto.CourseRequest;
import ms.tp.gestionecole.dto.CourseResponse;
import ms.tp.gestionecole.entity.Course;

@Component
public class CourseConverter {

    public Course toEntity(CourseRequest req) {
        return new Course(req.code(), req.title(), req.capacity());
    }

    public void updateEntity(Course course, CourseRequest req) {
        course.setCode(req.code());
        course.setTitle(req.title());
        course.setCapacity(req.capacity());
    }

    public CourseResponse toResponse(Course c, long enrolled) {
        return new CourseResponse(c.getId(), c.getCode(), c.getTitle(), c.getCapacity(),
                enrolled, Math.max(0, c.getCapacity() - enrolled));
    }
}
