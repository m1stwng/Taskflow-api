package dev.m1stwng.taskflow.auth.service;

import dev.m1stwng.taskflow.auth.dto.request.RegisterRequest;
import dev.m1stwng.taskflow.auth.dto.response.AuthenticationResponse;
import dev.m1stwng.taskflow.auth.exception.DuplicateEmailException;
import dev.m1stwng.taskflow.common.normalization.EmailNormalizer;
import dev.m1stwng.taskflow.fixture.UserFixture;
import dev.m1stwng.taskflow.security.entity.SecurityUser;
import dev.m1stwng.taskflow.security.service.JwtService;
import dev.m1stwng.taskflow.user.entity.User;
import dev.m1stwng.taskflow.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static dev.m1stwng.taskflow.fixture.UserFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    private final String ACCESS_TOKEN = "MOCK-ACCESS-TOKEN";

    @Mock
    private EmailNormalizer emailNormalizer;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Nested
    class Register {
        final RegisterRequest request = new RegisterRequest(USER_NAME, USER_EMAIL, USER_PASSWORD);

        @Test
        void shouldRegister() {
            final User user = UserFixture.user();

            when(emailNormalizer.normalize(USER_EMAIL)).thenReturn(USER_EMAIL);
            when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(false);
            when(passwordEncoder.encode(USER_PASSWORD)).thenReturn(USER_ENCODED_PASSWORD);
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(jwtService.generate(any(SecurityUser.class))).thenReturn(ACCESS_TOKEN);

            final AuthenticationResponse result = authService.register(request);

            verify(emailNormalizer).normalize(USER_EMAIL);
            verify(userRepository).existsByEmail(USER_EMAIL);
            verify(passwordEncoder).encode(USER_PASSWORD);
            verify(userRepository).save(userArgumentCaptor.capture());
            verify(jwtService).generate(any(SecurityUser.class));

            assertAll(
                    () -> assertEquals(USER_ID, result.id()),
                    () -> assertEquals(USER_NAME, result.name()),
                    () -> assertEquals(USER_EMAIL, result.email()),
                    () -> assertEquals(ACCESS_TOKEN, result.accessToken())
            );

            final User userBeforeSaving = userArgumentCaptor.getValue();

            assertAll(
                    () -> assertEquals(USER_NAME, userBeforeSaving.getName()),
                    () -> assertEquals(USER_EMAIL, userBeforeSaving.getEmail()),
                    () -> assertEquals(USER_ENCODED_PASSWORD, userBeforeSaving.getPassword())
            );

            verifyNoMoreInteractions(emailNormalizer, userRepository, passwordEncoder, jwtService);
        }

        @Test
        void shouldThrowWhenEmailAlreadyExists() {
            when(emailNormalizer.normalize(USER_EMAIL)).thenReturn(USER_EMAIL);
            when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(true);

            final DuplicateEmailException ex = assertThrows(
                    DuplicateEmailException.class,
                    () -> authService.register(request)
            );

            assertEquals("User with email %s already exists".formatted(USER_EMAIL), ex.getMessage());

            verify(emailNormalizer).normalize(USER_EMAIL);
            verify(userRepository).existsByEmail(USER_EMAIL);

            verifyNoInteractions(passwordEncoder, jwtService);
            verifyNoMoreInteractions(emailNormalizer, userRepository);
        }
    }
}
