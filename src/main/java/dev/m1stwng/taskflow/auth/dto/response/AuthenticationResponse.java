package dev.m1stwng.taskflow.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record AuthenticationResponse(
        UUID id,

        String name,

        @Schema(example = "user@example.com")
        String email,

        @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.signature")
        String accessToken
) {
}
