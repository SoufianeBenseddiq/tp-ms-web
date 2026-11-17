package ms.tp.gestionecole;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class GestionEcoleApiTests {

    @Autowired
    MockMvc mvc;

    private ResultActions postJson(String url, String body) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private ResultActions putJson(String url, String body) throws Exception {
        return mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private long idOf(ResultActions r) throws Exception {
        return ((Number) JsonPath.read(r.andReturn().getResponse().getContentAsString(), "$.id")).longValue();
    }

    private long createStudent(String firstName) throws Exception {
        String email = firstName.toLowerCase() + "-" + UUID.randomUUID() + "@ecole.ma";
        return idOf(postJson("/api/students", """
                {"firstName":"%s","lastName":"Test","email":"%s","birthDate":"2003-05-10"}
                """.formatted(firstName, email)).andExpect(status().isCreated()));
    }

    private long createCourse(int capacity) throws Exception {
        String code = "M" + UUID.randomUUID().toString().substring(0, 8);
        return idOf(postJson("/api/courses", """
                {"code":"%s","title":"Architecture Microservices","capacity":%d}
                """.formatted(code, capacity)).andExpect(status().isCreated()));
    }

    @Test
    void studentManagement_createUpdateRead() throws Exception {
        long id = createStudent("Amine");

        putJson("/api/students/" + id, """
                {"firstName":"Amine","lastName":"Alaoui","email":"amine.alaoui@ecole.ma","birthDate":"2003-05-10"}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Alaoui"));

        mvc.perform(get("/api/students/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("amine.alaoui@ecole.ma"));

        mvc.perform(get("/api/students/999999")).andExpect(status().isNotFound());

        // email en doublon
        postJson("/api/students", """
                {"firstName":"X","lastName":"Y","email":"amine.alaoui@ecole.ma"}
                """).andExpect(status().isConflict());

        // validation
        postJson("/api/students", """
                {"firstName":"","lastName":"Y","email":"pas-un-email"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.firstName").exists());
    }

    @Test
    void registration_respectsCapacityAndUniqueness() throws Exception {
        long courseId = createCourse(2);
        long s1 = createStudent("A");
        long s2 = createStudent("B");
        long s3 = createStudent("C");

        String reg = "{\"studentId\":%d,\"courseId\":%d}";
        postJson("/api/registrations", reg.formatted(s1, courseId)).andExpect(status().isCreated());
        postJson("/api/registrations", reg.formatted(s1, courseId)).andExpect(status().isConflict()); // déjà inscrit
        postJson("/api/registrations", reg.formatted(s2, courseId)).andExpect(status().isCreated());
        postJson("/api/registrations", reg.formatted(s3, courseId)).andExpect(status().isConflict()); // complet

        mvc.perform(get("/api/courses/" + courseId))
                .andExpect(jsonPath("$.enrolled").value(2))
                .andExpect(jsonPath("$.availableSeats").value(0));

        mvc.perform(get("/api/registrations").param("courseId", String.valueOf(courseId)))
                .andExpect(jsonPath("$.length()").value(2));

        // impossible de réduire la capacité sous le nombre d'inscrits
        putJson("/api/courses/" + courseId, """
                {"code":"MX-%d","title":"Archi","capacity":1}
                """.formatted(courseId)).andExpect(status().isConflict());
    }

    @Test
    void examAndGrades() throws Exception {
        long courseId = createCourse(10);
        long registered = createStudent("Inscrit");
        long notRegistered = createStudent("Externe");
        postJson("/api/registrations", "{\"studentId\":%d,\"courseId\":%d}".formatted(registered, courseId))
                .andExpect(status().isCreated());

        long examId = idOf(postJson("/api/exams", """
                {"courseId":%d,"title":"Examen final","examDate":"2026-12-20T09:00:00","room":"Amphi A"}
                """.formatted(courseId)).andExpect(status().isCreated()));

        mvc.perform(get("/api/exams").param("courseId", String.valueOf(courseId)))
                .andExpect(jsonPath("$.length()").value(1));

        String grade = "{\"examId\":%d,\"studentId\":%d,\"score\":%s}";
        long gradeId = idOf(postJson("/api/grades", grade.formatted(examId, registered, "14.5"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(14.5)));

        postJson("/api/grades", grade.formatted(examId, registered, "15")).andExpect(status().isConflict());
        postJson("/api/grades", grade.formatted(examId, notRegistered, "12")).andExpect(status().isConflict());
        postJson("/api/grades", grade.formatted(examId, registered, "25")).andExpect(status().isBadRequest());

        putJson("/api/grades/" + gradeId, "{\"score\":16}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(16));

        mvc.perform(get("/api/grades").param("studentId", String.valueOf(registered)))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].examTitle").value("Examen final"));
    }
}
