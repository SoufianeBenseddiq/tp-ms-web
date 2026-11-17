package ms.tp.gestionecole.student;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public final class StudentDtos {

    private StudentDtos() {
    }

    public record StudentRequest(
            @NotBlank @Size(max = 100) String firstName,
            @NotBlank @Size(max = 100) String lastName,
            @NotBlank @Email @Size(max = 150) String email,
            @Past LocalDate birthDate) {
    }

    public record StudentResponse(Long id, String firstName, String lastName, String email, LocalDate birthDate) {

        public static StudentResponse from(Student s) {
            return new StudentResponse(s.getId(), s.getFirstName(), s.getLastName(), s.getEmail(), s.getBirthDate());
        }
    }
}
