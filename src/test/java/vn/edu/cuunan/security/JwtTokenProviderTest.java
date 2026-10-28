package vn.edu.cuunan.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMs", 3600000L); // 1h
        ReflectionTestUtils.setField(tokenProvider, "cookieName", "jwt_token");
    }

    @Test
    @DisplayName("Tạo JWT token và trích xuất đúng thông tin username, role, fullName")
    void testGenerateAndParseToken() {
        String token = tokenProvider.generateToken("vovanthinh", "ROLE_CITIZEN", "Võ Văn Thịnh");
        assertNotNull(token);
        assertTrue(token.length() > 20);

        assertTrue(tokenProvider.validateToken(token));
        assertEquals("vovanthinh", tokenProvider.getUsernameFromToken(token));
        assertEquals("ROLE_CITIZEN", tokenProvider.getRoleFromToken(token));
        assertEquals("Võ Văn Thịnh", tokenProvider.getFullNameFromToken(token));
    }

    @Test
    @DisplayName("Validate token giả mạo hoặc lỗi trả về false")
    void testInvalidToken() {
        assertFalse(tokenProvider.validateToken("invalid.jwt.token"));
        assertFalse(tokenProvider.validateToken(""));
        assertFalse(tokenProvider.validateToken(null));
    }
}
