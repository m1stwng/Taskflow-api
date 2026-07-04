package dev.m1stwng.taskflow.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dev.m1stwng.taskflow.security.entity.SecurityUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Log4j2
@Service
public class JwtService {

    private final Algorithm algorithm;
    private final Clock clock;
    private final long expirationSeconds;

    public JwtService(
            Clock clock,
            @Value("${jwt.expiration-seconds}") long expirationSeconds,
            @Value("${jwt.secret}") String secret
    ) {
        algorithm = Algorithm.HMAC256(secret);
        this.clock = clock;
        this.expirationSeconds = expirationSeconds;
    }

    public String generate(SecurityUser securityUser) {
        log.info("Generating access token for user {}", securityUser.id());

        final String accessToken = JWT.create()
                .withSubject(securityUser.email())
                .withClaim("id", String.valueOf(securityUser.id()))
                .withIssuedAt(Instant.now(clock))
                .withExpiresAt(Instant.now(clock).plusSeconds(expirationSeconds))
                .sign(algorithm);

        log.info("Access token generated");

        return accessToken;
    }
}
