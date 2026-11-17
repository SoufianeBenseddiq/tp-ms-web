package ms.tp.gestionecole.converter;

import org.springframework.stereotype.Component;

import ms.tp.gestionecole.dto.GradeResponse;
import ms.tp.gestionecole.entity.Grade;

@Component
public class GradeConverter {

    public GradeResponse toResponse(Grade g) {
        return new GradeResponse(g.getId(),
                g.getExam().getId(), g.getExam().getTitle(), g.getExam().getCourse().getCode(),
                g.getStudent().getId(),
                g.getStudent().getFirstName() + " " + g.getStudent().getLastName(),
                g.getScore(), g.getRecordedAt());
    }
}
