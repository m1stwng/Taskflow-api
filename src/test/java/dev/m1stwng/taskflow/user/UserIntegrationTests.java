package dev.m1stwng.taskflow.user;

import dev.m1stwng.taskflow.TestcontainersConfiguration;
import dev.m1stwng.taskflow.fixture.UserFixture;
import dev.m1stwng.taskflow.security.entity.SecurityUser;
import dev.m1stwng.taskflow.user.entity.User;
import dev.m1stwng.taskflow.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static dev.m1stwng.taskflow.fixture.UserFixture.USER_EMAIL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class UserIntegrationTests {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private User user;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        user = UserFixture.user();
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class MeEndpoint {

        @Test
        void shouldReturnAuthenticatedUser() throws Exception {
            final User createdUser = userRepository.save(user);
            final SecurityUser securityUser = new SecurityUser(createdUser.getId(), createdUser.getEmail(), null);

            final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    securityUser, null, securityUser.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            mockMvc.perform(get("/api/users/me"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.id").isString())
                    .andExpect(jsonPath("$.name").value(createdUser.getName()))
                    .andExpect(jsonPath("$.email").value(createdUser.getEmail()));
        }

        @Test
        void shouldReturn404WhenUserNotFound() throws Exception {
            final SecurityUser securityUser = new SecurityUser(UUID.randomUUID(), USER_EMAIL, null);

            final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    securityUser, null, securityUser.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            mockMvc.perform(get("/api/users/me"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.detail").value("User was not found"))
                    .andExpect(jsonPath("$.instance").value("/api/users/me"))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.title").value("Not Found"));
        }
    }
}
