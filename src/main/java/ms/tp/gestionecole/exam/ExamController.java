package ms.tp.gestionecole.exam;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ms.tp.gestionecole.exam.ExamDtos.ExamRequest;
import ms.tp.gestionecole.exam.ExamDtos.ExamResponse;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService service;

    public ExamController(ExamService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExamResponse plan(@Valid @RequestBody ExamRequest req) {
        return service.planExam(req);
    }

    @GetMapping("/{id}")
    public ExamResponse findById(@PathVariable Long id) {
        return service.findExam(id);
    }

    /** GET /api/exams?courseId=1 (filtre optionnel) */
    @GetMapping
    public List<ExamResponse> findAll(@RequestParam(required = false) Long courseId) {
        return service.findExams(courseId);
    }
}
