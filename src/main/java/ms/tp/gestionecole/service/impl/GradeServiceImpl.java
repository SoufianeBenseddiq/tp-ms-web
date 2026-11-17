package ms.tp.gestionecole.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ms.tp.gestionecole.converter.GradeConverter;
import ms.tp.gestionecole.dto.GradeRequest;
import ms.tp.gestionecole.dto.GradeResponse;
import ms.tp.gestionecole.dto.GradeUpdateRequest;
import ms.tp.gestionecole.entity.Exam;
import ms.tp.gestionecole.entity.Grade;
import ms.tp.gestionecole.entity.Student;
import ms.tp.gestionecole.exception.BusinessException;
import ms.tp.gestionecole.exception.ResourceNotFoundException;
import ms.tp.gestionecole.repository.ExamRepository;
import ms.tp.gestionecole.repository.GradeRepository;
import ms.tp.gestionecole.repository.RegistrationRepository;
import ms.tp.gestionecole.repository.StudentRepository;
import ms.tp.gestionecole.service.facade.GradeService;

@Service
@Transactional
public class GradeServiceImpl implements GradeService {

    private final GradeRepository repository;
    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final RegistrationRepository registrationRepository;
    private final GradeConverter converter;

    public GradeServiceImpl(GradeRepository repository, ExamRepository examRepository,
                            StudentRepository studentRepository, RegistrationRepository registrationRepository,
                            GradeConverter converter) {
        this.repository = repository;
        this.examRepository = examRepository;
        this.studentRepository = studentRepository;
        this.registrationRepository = registrationRepository;
        this.converter = converter;
    }

    @Override
    public GradeResponse record(GradeRequest req) {
        Exam exam = examRepository.findById(req.examId())
                .orElseThrow(() -> new ResourceNotFoundException("Examen", req.examId()));
        Student student = studentRepository.findById(req.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant", req.studentId()));
        Long courseId = exam.getCourse().getId();

        if (!registrationRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new BusinessException("L'étudiant " + student.getId()
                    + " n'est pas inscrit au module " + exam.getCourse().getCode());
        }
        if (repository.existsByExamIdAndStudentId(exam.getId(), student.getId())) {
            throw new BusinessException("Une note existe déjà pour cet étudiant à cet examen (utiliser PUT pour la modifier)");
        }
        return converter.toResponse(repository.save(new Grade(exam, student, req.score())));
    }

    @Override
    public GradeResponse update(Long id, GradeUpdateRequest req) {
        Grade grade = getEntity(id);
        grade.setScore(req.score());
        return converter.toResponse(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public GradeResponse findById(Long id) {
        return converter.toResponse(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> search(Long examId, Long studentId) {
        List<Grade> grades;
        if (examId != null && studentId != null) {
            grades = repository.findByExamIdAndStudentId(examId, studentId);
        } else if (examId != null) {
            grades = repository.findByExamId(examId);
        } else if (studentId != null) {
            grades = repository.findByStudentId(studentId);
        } else {
            grades = repository.findAll();
        }
        return grades.stream().map(converter::toResponse).toList();
    }

    private Grade getEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Note", id));
    }
}
