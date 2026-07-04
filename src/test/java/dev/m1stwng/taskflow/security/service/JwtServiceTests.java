package dev.m1stwng.taskflow.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.m1stwng.taskflow.fixture.UserFixture;
import dev.m1stwng.taskflow.security.entity.SecurityUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class JwtServiceTests {

    private final Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

    private final long EXPIRATION_SECONDS = 900L;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(clock, EXPIRATION_SECONDS, "my-test-secret");
    }

    @Nested
    class Generate {

        @Test
        void shouldGenerate() {
            final SecurityUser securityUser = SecurityUser.of(UserFixture.user());

            final String accessToken = jwtService.generate(securityUser);
            final DecodedJWT decoded = JWT.decode(accessToken);

            assertAll(
                    () -> assertEquals(securityUser.email(), decoded.getSubject()),
                    () -> assertEquals(securityUser.id(), UUID.fromString(decoded.getClaim("id").asString())),
                    () -> assertEquals(clock.instant(), decoded.getIssuedAtAsInstant()),
                    () -> assertEquals(clock.instant().plusSeconds(EXPIRATION_SECONDS), decoded.getExpiresAtAsInstant())
            );
        }
    }
}
