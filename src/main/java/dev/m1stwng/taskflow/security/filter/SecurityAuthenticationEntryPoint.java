package dev.m1stwng.taskflow.security.filter;

import dev.m1stwng.taskflow.security.exception.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@Component
@Log4j2
@NullMarked
@RequiredArgsConstructor
public class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);

        problemDetail.setInstance(URI.create(request.getRequestURI()));

        if (authException instanceof JwtException) {
            log.warn("Access token is invalid");

            problemDetail.setDetail("Invalid JWT access token");
        } else {
            log.warn("Authentication is required for {}", request.getRequestURI());

            problemDetail.setDetail("Authentication required");
        }

        response.setContentType(APPLICATION_PROBLEM_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        objectMapper.writeValue(response.getWriter(), problemDetail);
    }
}
