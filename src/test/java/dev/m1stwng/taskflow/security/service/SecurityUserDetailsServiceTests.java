package dev.m1stwng.taskflow.security.service;

import dev.m1stwng.taskflow.fixture.UserFixture;
import dev.m1stwng.taskflow.security.entity.SecurityUser;
import dev.m1stwng.taskflow.user.entity.User;
import dev.m1stwng.taskflow.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static dev.m1stwng.taskflow.fixture.UserFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class SecurityUserDetailsServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityUserDetailsService securityUserDetailsService;

    @Nested
    class LoadUserByUsername {

        @Test
        void shouldLoadUserByEmail() {
            final User user = UserFixture.user();

            when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

            final SecurityUser result = securityUserDetailsService.loadUserByUsername(USER_EMAIL);

            verify(userRepository).findByEmail(USER_EMAIL);

            assertAll(
                    () -> assertEquals(USER_ID, result.id()),
                    () -> assertEquals(USER_EMAIL, result.email()),
                    () -> assertEquals(USER_ENCODED_PASSWORD, result.password())
            );

            verifyNoMoreInteractions(userRepository);
        }

        @Test
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

            final UsernameNotFoundException ex = assertThrows(
                    UsernameNotFoundException.class,
                    () -> securityUserDetailsService.loadUserByUsername(USER_EMAIL)
            );

            verify(userRepository).findByEmail(USER_EMAIL);

            assertEquals("User with email %s was not found".formatted(USER_EMAIL), ex.getMessage());

            verifyNoMoreInteractions(userRepository);
        }
    }
}
