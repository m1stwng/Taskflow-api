package dev.m1stwng.taskflow.user.controller;

import dev.m1stwng.taskflow.config.openapi.NotFoundApiResponse;
import dev.m1stwng.taskflow.config.openapi.UnauthorizedApiResponse;
import dev.m1stwng.taskflow.security.entity.SecurityUser;
import dev.m1stwng.taskflow.user.dto.response.UserResponse;
import dev.m1stwng.taskflow.user.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/users")
@RequiredArgsConstructor
@RestController
@Tag(name = "Users", description = "Endpoints for user management")
public class UserController {

    private final UserService userService;

    @ApiResponse(responseCode = "200")
    @UnauthorizedApiResponse
    @NotFoundApiResponse
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal SecurityUser securityUser) {
        final UserResponse response = userService.findByUserId(securityUser.id());

        return ResponseEntity.ok(response);
    }
}
