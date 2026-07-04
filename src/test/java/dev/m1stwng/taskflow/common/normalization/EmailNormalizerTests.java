package dev.m1stwng.taskflow.common.normalization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static dev.m1stwng.taskflow.fixture.UserFixture.USER_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class EmailNormalizerTests {

    private EmailNormalizer emailNormalizer;

    @BeforeEach
    void setUp() {
        emailNormalizer = new EmailNormalizer();
    }

    @Nested
    class Normalize {

        @Test
        void shouldNormalize() {
            final String unnormalizedEmail = "   JoHn@ExAmPlE.COM   ";

            final String result = emailNormalizer.normalize(unnormalizedEmail);

            assertEquals(USER_EMAIL, result);
        }
    }
}
