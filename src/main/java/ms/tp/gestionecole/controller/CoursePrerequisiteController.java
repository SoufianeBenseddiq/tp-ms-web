package ms.tp.gestionecole.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ms.tp.gestionecole.dto.CoursePrerequisiteRequest;
import ms.tp.gestionecole.dto.CoursePrerequisiteResponse;
import ms.tp.gestionecole.service.facade.CoursePrerequisiteService;

@RestController
@RequestMapping("/api/courses/{courseId}/prerequisites")
public class CoursePrerequisiteController {

    private final CoursePrerequisiteService service;

    public CoursePrerequisiteController(CoursePrerequisiteService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CoursePrerequisiteResponse add(@PathVariable Long courseId,
                                          @Valid @RequestBody CoursePrerequisiteRequest req) {
        return service.add(courseId, req);
    }

    @GetMapping
    public List<CoursePrerequisiteResponse> findByCourse(@PathVariable Long courseId) {
        return service.findByCourse(courseId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long courseId, @PathVariable Long id) {
        service.delete(courseId, id);
    }
}
