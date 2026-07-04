package dev.m1stwng.taskflow.auth.controller;

import dev.m1stwng.taskflow.auth.dto.request.RegisterRequest;
import dev.m1stwng.taskflow.auth.dto.response.AuthenticationResponse;
import dev.m1stwng.taskflow.auth.service.AuthService;
import dev.m1stwng.taskflow.config.openapi.BadRequestApiResponse;
import dev.m1stwng.taskflow.config.openapi.ConflictApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
@SecurityRequirements
@Tag(name = "Auth", description = "Endpoints for authentication management")
public class AuthController {

    private final AuthService authService;

    @ApiResponse(responseCode = "201")
    @BadRequestApiResponse
    @ConflictApiResponse
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest request) {
        final AuthenticationResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
