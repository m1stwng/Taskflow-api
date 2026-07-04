package dev.m1stwng.taskflow.security.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtException extends AuthenticationException {
    public JwtException(String message) {
        super(message);
    }
}
