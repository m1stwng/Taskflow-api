package dev.m1stwng.taskflow.auth.service;

import dev.m1stwng.taskflow.auth.dto.request.RegisterRequest;
import dev.m1stwng.taskflow.auth.dto.response.AuthenticationResponse;
import dev.m1stwng.taskflow.auth.exception.DuplicateEmailException;
import dev.m1stwng.taskflow.common.normalization.EmailNormalizer;
import dev.m1stwng.taskflow.security.entity.SecurityUser;
import dev.m1stwng.taskflow.security.service.JwtService;
import dev.m1stwng.taskflow.user.entity.User;
import dev.m1stwng.taskflow.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class AuthService {

    private final EmailNormalizer emailNormalizer;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthenticationResponse register(RegisterRequest request) {
        final String email = emailNormalizer.normalize(request.email());

        log.info("Registering user {}", email);

        final boolean exists = userRepository.existsByEmail(email);

        if (exists) {
            log.warn("Failed registration, duplicated email for {}", email);

            throw new DuplicateEmailException("User with email %s already exists".formatted(email));
        }

        final User user = User.builder()
                .name(request.name())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .build();

        final User createdUser = userRepository.save(user);

        log.info("User registered with id {}", createdUser.getId());

        final String accessToken = jwtService.generate(SecurityUser.of(createdUser));

        return new AuthenticationResponse(
                createdUser.getId(),
                createdUser.getName(),
                createdUser.getEmail(),
                accessToken
        );
    }
}
