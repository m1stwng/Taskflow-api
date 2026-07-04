package dev.m1stwng.taskflow.fixture;

import dev.m1stwng.taskflow.user.entity.User;

import java.util.UUID;

public class UserFixture {

    private UserFixture() {

    }

    public static final UUID USER_ID = UUID.fromString("2214a8c4-ee17-4765-8f48-8563a6f399f7");
    public static final String USER_NAME = "John Doe";
    public static final String USER_EMAIL = "john@example.com";
    public static final String USER_PASSWORD = "password123";
    public static final String USER_ENCODED_PASSWORD = "encoded-password";

    public static User user() {
        final User user = User.builder()
                .name(USER_NAME)
                .email(USER_EMAIL)
                .password(USER_ENCODED_PASSWORD)
                .build();

        user.setId(USER_ID);

        return user;
    }
}
