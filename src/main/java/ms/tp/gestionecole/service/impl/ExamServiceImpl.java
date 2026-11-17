package ms.tp.gestionecole.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ms.tp.gestionecole.converter.ExamConverter;
import ms.tp.gestionecole.dto.ExamRequest;
import ms.tp.gestionecole.dto.ExamResponse;
import ms.tp.gestionecole.entity.Course;
import ms.tp.gestionecole.entity.Exam;
import ms.tp.gestionecole.exception.ResourceNotFoundException;
import ms.tp.gestionecole.repository.CourseRepository;
import ms.tp.gestionecole.repository.ExamRepository;
import ms.tp.gestionecole.service.facade.ExamService;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    private final ExamRepository repository;
    private final CourseRepository courseRepository;
    private final ExamConverter converter;

    public ExamServiceImpl(ExamRepository repository, CourseRepository courseRepository, ExamConverter converter) {
        this.repository = repository;
        this.courseRepository = courseRepository;
        this.converter = converter;
    }

    @Override
    public ExamResponse planExam(ExamRequest req) {
        Course course = courseRepository.findById(req.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Module", req.courseId()));
        return converter.toResponse(repository.save(converter.toEntity(req, course)));
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponse findById(Long id) {
        Exam exam = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Examen", id));
        return converter.toResponse(exam);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> findAll(Long courseId) {
        List<Exam> exams = courseId != null
                ? repository.findByCourseIdOrderByExamDateAsc(courseId)
                : repository.findAll();
        return exams.stream().map(converter::toResponse).toList();
    }
}
