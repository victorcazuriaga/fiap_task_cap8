package br.com.ecogov.compliance.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "unit-test-secret-1234567890");
    }

    @Test
    void generateToken_shouldReturnNonEmptyJwt() {
        String token = tokenService.generateToken("12345678000100");

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void validateToken_shouldReturnSubjectWhenValid() {
        String cnpj = "12345678000100";
        String token = tokenService.generateToken(cnpj);

        String subject = tokenService.validateToken(token);

        assertThat(subject).isEqualTo(cnpj);
    }

    @Test
    void validateToken_shouldReturnEmptyWhenTokenIsInvalid() {
        String result = tokenService.validateToken("not-a-real-token");

        assertThat(result).isEmpty();
    }

    @Test
    void validateToken_shouldReturnEmptyWhenSecretMismatches() {
        String token = tokenService.generateToken("12345678000100");

        TokenService otherService = new TokenService();
        ReflectionTestUtils.setField(otherService, "secret", "different-secret");

        assertThat(otherService.validateToken(token)).isEmpty();
    }
}
