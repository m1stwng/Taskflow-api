package dev.m1stwng.taskflow.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record UserResponse(
        UUID id,

        String name,

        @Schema(example = "user@example.com")
        String email
) {
}
