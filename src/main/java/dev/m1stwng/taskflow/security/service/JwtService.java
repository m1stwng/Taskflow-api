package dev.m1stwng.taskflow.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import dev.m1stwng.taskflow.security.entity.SecurityUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

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

    public SecurityUser verify(String accessToken) {
        log.info("Verifying access token");

        final JWTVerifier verifier = JWT.require(algorithm).build();

        final Map<String, Claim> claims = verifier.verify(accessToken).getClaims();

        log.info("Access token verified");

        return new SecurityUser(
                UUID.fromString(claims.get("id").asString()),
                claims.get("sub").asString(),
                null
        );
    }
}
