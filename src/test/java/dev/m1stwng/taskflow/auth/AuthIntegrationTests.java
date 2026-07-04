package dev.m1stwng.taskflow.auth;

import dev.m1stwng.taskflow.auth.dto.request.LoginRequest;
import dev.m1stwng.taskflow.auth.dto.request.RegisterRequest;
import dev.m1stwng.taskflow.fixture.UserFixture;
import dev.m1stwng.taskflow.user.entity.User;
import dev.m1stwng.taskflow.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;
import tools.jackson.databind.ObjectMapper;

import static dev.m1stwng.taskflow.fixture.UserFixture.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = TestcontainersConfiguration.class)
public abstract class AuthIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserFixture.user();
        user.setId(null);
        user.setPassword(passwordEncoder.encode(USER_PASSWORD));

        userRepository.deleteAll();
    }

    @Nested
    class LoginEndpoint {
        final LoginRequest request = new LoginRequest(USER_EMAIL, USER_PASSWORD);

        @Test
        void shouldLogin() throws Exception {
            userRepository.save(user);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.id").isString())
                    .andExpect(jsonPath("$.name").value(USER_NAME))
                    .andExpect(jsonPath("$.email").value(USER_EMAIL))
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.accessToken").isString());
        }

        @Test
        void shouldReturn401WhenBadCredentials() throws Exception {
            userRepository.save(user);

            final LoginRequest wrongRequest = new LoginRequest(USER_EMAIL, "wrong-password");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(wrongRequest))
                    )
                    .andExpect(status().isConflict())
                    .andExpect(content().contentTypeCompatibleWith(APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.detail").value("Email or password invalid"))
                    .andExpect(jsonPath("$.instance").value("/api/auth/login"))
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.title").value("Bad credentials"));
        }
    }

    @Nested
    class RegisterEndpoint {
        final RegisterRequest request = new RegisterRequest(USER_NAME, USER_EMAIL, USER_PASSWORD);

        @Test
        void shouldRegister() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.id").isString())
                    .andExpect(jsonPath("$.name").value(USER_NAME))
                    .andExpect(jsonPath("$.email").value(USER_EMAIL))
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.accessToken").isString());

            assertEquals(1, userRepository.count());
        }

        @Test
        void shouldReturn409WhenEmailAlreadyExists() throws Exception {
            userRepository.save(user);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isConflict())
                    .andExpect(content().contentTypeCompatibleWith(APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.detail").value("Email already exists"))
                    .andExpect(jsonPath("$.instance").value("/api/auth/register"))
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.title").value("Duplicated email"));

            assertEquals(1, userRepository.count());
        }
    }
}
