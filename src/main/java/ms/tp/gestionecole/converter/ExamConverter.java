package ms.tp.gestionecole.converter;

import org.springframework.stereotype.Component;

import ms.tp.gestionecole.dto.ExamRequest;
import ms.tp.gestionecole.dto.ExamResponse;
import ms.tp.gestionecole.entity.Course;
import ms.tp.gestionecole.entity.Exam;

@Component
public class ExamConverter {

    public Exam toEntity(ExamRequest req, Course course) {
        return new Exam(course, req.title(), req.examDate(), req.room());
    }

    public ExamResponse toResponse(Exam e) {
        return new ExamResponse(e.getId(), e.getCourse().getId(), e.getCourse().getCode(),
                e.getTitle(), e.getExamDate(), e.getRoom());
    }
}
