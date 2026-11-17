package ms.tp.gestionecole.exam;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ms.tp.gestionecole.exam.ExamDtos.GradeRequest;
import ms.tp.gestionecole.exam.ExamDtos.GradeResponse;
import ms.tp.gestionecole.exam.ExamDtos.GradeUpdateRequest;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final ExamService service;

    public GradeController(ExamService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GradeResponse record(@Valid @RequestBody GradeRequest req) {
        return service.recordGrade(req);
    }

    @PutMapping("/{id}")
    public GradeResponse update(@PathVariable Long id, @Valid @RequestBody GradeUpdateRequest req) {
        return service.updateGrade(id, req);
    }

    @GetMapping("/{id}")
    public GradeResponse findById(@PathVariable Long id) {
        return service.findGrade(id);
    }

    /** GET /api/grades?examId=1&studentId=2 (filtres optionnels) */
    @GetMapping
    public List<GradeResponse> search(@RequestParam(required = false) Long examId,
                                      @RequestParam(required = false) Long studentId) {
        return service.findGrades(examId, studentId);
    }
}
