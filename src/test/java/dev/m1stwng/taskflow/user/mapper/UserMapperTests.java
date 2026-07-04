package dev.m1stwng.taskflow.user.mapper;

import dev.m1stwng.taskflow.fixture.UserFixture;
import dev.m1stwng.taskflow.user.dto.response.UserResponse;
import dev.m1stwng.taskflow.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static dev.m1stwng.taskflow.fixture.UserFixture.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
public class UserMapperTests {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
    }

    @Nested
    class ToResponse {

        @Test
        void shouldMapToResponse() {
            final User user = UserFixture.user();

            final UserResponse response = userMapper.toResponse(user);

            assertAll(
                    () -> assertEquals(USER_ID, response.id()),
                    () -> assertEquals(USER_NAME, response.name()),
                    () -> assertEquals(USER_EMAIL, response.email())
            );
        }
    }
}
