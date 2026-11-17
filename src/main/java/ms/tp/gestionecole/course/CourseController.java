package ms.tp.gestionecole.course;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ms.tp.gestionecole.course.CourseDtos.CourseRequest;
import ms.tp.gestionecole.course.CourseDtos.CourseResponse;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse create(@Valid @RequestBody CourseRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public CourseResponse update(@PathVariable Long id, @Valid @RequestBody CourseRequest req) {
        return service.update(id, req);
    }

    @GetMapping("/{id}")
    public CourseResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<CourseResponse> findAll() {
        return service.findAll();
    }
}
