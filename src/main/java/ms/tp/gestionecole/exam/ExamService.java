package ms.tp.gestionecole.exam;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ms.tp.gestionecole.common.BusinessException;
import ms.tp.gestionecole.common.ResourceNotFoundException;
import ms.tp.gestionecole.course.Course;
import ms.tp.gestionecole.course.CourseService;
import ms.tp.gestionecole.exam.ExamDtos.ExamRequest;
import ms.tp.gestionecole.exam.ExamDtos.ExamResponse;
import ms.tp.gestionecole.exam.ExamDtos.GradeRequest;
import ms.tp.gestionecole.exam.ExamDtos.GradeResponse;
import ms.tp.gestionecole.exam.ExamDtos.GradeUpdateRequest;
import ms.tp.gestionecole.registration.RegistrationService;
import ms.tp.gestionecole.student.Student;
import ms.tp.gestionecole.student.StudentService;

@Service
@Transactional
public class ExamService {

    private final ExamRepository examRepository;
    private final GradeRepository gradeRepository;
    private final CourseService courseService;
    private final StudentService studentService;
    private final RegistrationService registrationService;

    public ExamService(ExamRepository examRepository, GradeRepository gradeRepository,
                       CourseService courseService, StudentService studentService,
                       RegistrationService registrationService) {
        this.examRepository = examRepository;
        this.gradeRepository = gradeRepository;
        this.courseService = courseService;
        this.studentService = studentService;
        this.registrationService = registrationService;
    }

    // ---------- Examens ----------

    public ExamResponse planExam(ExamRequest req) {
        Course course = courseService.getEntity(req.courseId());
        Exam exam = new Exam(course, req.title(), req.examDate(), req.room());
        return ExamResponse.from(examRepository.save(exam));
    }

    @Transactional(readOnly = true)
    public ExamResponse findExam(Long id) {
        return ExamResponse.from(getExam(id));
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> findExams(Long courseId) {
        List<Exam> exams = courseId != null
                ? examRepository.findByCourseIdOrderByExamDateAsc(courseId)
                : examRepository.findAll();
        return exams.stream().map(ExamResponse::from).toList();
    }

    // ---------- Notes ----------

    public GradeResponse recordGrade(GradeRequest req) {
        Exam exam = getExam(req.examId());
        Student student = studentService.getEntity(req.studentId());
        Long courseId = exam.getCourse().getId();

        if (!registrationService.isRegistered(student.getId(), courseId)) {
            throw new BusinessException("L'étudiant " + student.getId()
                    + " n'est pas inscrit au module " + exam.getCourse().getCode());
        }
        if (gradeRepository.existsByExamIdAndStudentId(exam.getId(), student.getId())) {
            throw new BusinessException("Une note existe déjà pour cet étudiant à cet examen (utiliser PUT pour la modifier)");
        }
        return GradeResponse.from(gradeRepository.save(new Grade(exam, student, req.score())));
    }

    public GradeResponse updateGrade(Long id, GradeUpdateRequest req) {
        Grade grade = getGrade(id);
        grade.setScore(req.score());
        return GradeResponse.from(grade);
    }

    @Transactional(readOnly = true)
    public GradeResponse findGrade(Long id) {
        return GradeResponse.from(getGrade(id));
    }

    @Transactional(readOnly = true)
    public List<GradeResponse> findGrades(Long examId, Long studentId) {
        List<Grade> grades;
        if (examId != null && studentId != null) {
            grades = gradeRepository.findByExamIdAndStudentId(examId, studentId);
        } else if (examId != null) {
            grades = gradeRepository.findByExamId(examId);
        } else if (studentId != null) {
            grades = gradeRepository.findByStudentId(studentId);
        } else {
            grades = gradeRepository.findAll();
        }
        return grades.stream().map(GradeResponse::from).toList();
    }

    private Exam getExam(Long id) {
        return examRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Examen", id));
    }

    private Grade getGrade(Long id) {
        return gradeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Note", id));
    }
}
