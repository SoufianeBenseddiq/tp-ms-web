package ms.tp.gestionecole.registration;

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
import ms.tp.gestionecole.registration.RegistrationDtos.RegistrationRequest;
import ms.tp.gestionecole.registration.RegistrationDtos.RegistrationResponse;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService service;

    public RegistrationController(RegistrationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse register(@Valid @RequestBody RegistrationRequest req) {
        return service.register(req);
    }

    @GetMapping("/{id}")
    public RegistrationResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    /** GET /api/registrations?studentId=1&courseId=2 (filtres optionnels) */
    @GetMapping
    public List<RegistrationResponse> search(@RequestParam(required = false) Long studentId,
                                             @RequestParam(required = false) Long courseId) {
        return service.search(studentId, courseId);
    }
}
