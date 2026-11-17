package ms.tp.gestionecole.converter;

import org.springframework.stereotype.Component;

import ms.tp.gestionecole.dto.CoursePrerequisiteResponse;
import ms.tp.gestionecole.entity.Course;
import ms.tp.gestionecole.entity.CoursePrerequisite;

@Component
public class CoursePrerequisiteConverter {

    public CoursePrerequisite toEntity(Course course, Course prerequisite, Course equivalent) {
        return new CoursePrerequisite(course, prerequisite, equivalent);
    }

    public CoursePrerequisiteResponse toResponse(CoursePrerequisite p) {
        Course equivalent = p.getEquivalent();
        return new CoursePrerequisiteResponse(p.getId(),
                p.getCourse().getId(), p.getCourse().getCode(),
                p.getPrerequisite().getId(), p.getPrerequisite().getCode(),
                equivalent != null ? equivalent.getId() : null,
                equivalent != null ? equivalent.getCode() : null);
    }
}
