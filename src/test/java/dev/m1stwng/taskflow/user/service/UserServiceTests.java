package dev.m1stwng.taskflow.user.service;

import dev.m1stwng.taskflow.fixture.UserFixture;
import dev.m1stwng.taskflow.user.dto.response.UserResponse;
import dev.m1stwng.taskflow.user.entity.User;
import dev.m1stwng.taskflow.user.exception.UserNotFoundException;
import dev.m1stwng.taskflow.user.mapper.UserMapper;
import dev.m1stwng.taskflow.user.mapper.UserMapperImpl;
import dev.m1stwng.taskflow.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static dev.m1stwng.taskflow.fixture.UserFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    class FindUserById {

        @Test
        void shouldFindUserById() {
            final User user = UserFixture.user();

            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

            final UserResponse result = userService.findByUserId(USER_ID);

            verify(userRepository).findById(USER_ID);
            verify(userMapper).toResponse(user);

            assertAll(
                    () -> assertEquals(USER_ID, result.id()),
                    () -> assertEquals(USER_NAME, result.name()),
                    () -> assertEquals(USER_EMAIL, result.email())
            );

            verifyNoMoreInteractions(userRepository, userMapper);
        }

        @Test
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            final UserNotFoundException ex = assertThrows(
                    UserNotFoundException.class,
                    () -> userService.findByUserId(USER_ID)
            );

            verify(userRepository).findById(USER_ID);

            assertEquals("User with id %s not found".formatted(USER_ID), ex.getMessage());

            verifyNoInteractions(userMapper);
            verifyNoMoreInteractions(userRepository);
        }
    }
}
