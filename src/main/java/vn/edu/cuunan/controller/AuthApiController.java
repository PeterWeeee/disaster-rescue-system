package vn.edu.cuunan.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.cuunan.dto.AuthResponseDto;
import vn.edu.cuunan.dto.LoginRequestDto;
import vn.edu.cuunan.dto.RegisterRequestDto;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.security.CustomUserDetails;
import vn.edu.cuunan.security.JwtTokenProvider;
import vn.edu.cuunan.service.AuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;

    public AuthApiController(AuthService authService, JwtTokenProvider tokenProvider) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginDto,
                                   HttpServletResponse response) {
        AuthResponseDto authResponse = authService.authenticate(loginDto);

        // Đính kèm cookie HttpOnly cho client web
        Cookie jwtCookie = new Cookie(tokenProvider.getCookieName(), authResponse.getToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(86400);
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registerDto) {
        User createdUser = authService.register(registerDto);

        Map<String, Object> result = new HashMap<>();
        result.put("status", 201);
        result.put("message", "Đăng ký tài khoản thành công!");
        result.put("userId", createdUser.getId());
        result.put("username", createdUser.getUsername());
        result.put("role", createdUser.getRole().getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Chưa đăng nhập"));
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", userDetails.getId());
        profile.put("username", userDetails.getUsername());
        profile.put("fullName", userDetails.getFullName());
        profile.put("role", userDetails.getRoleName());
        profile.put("phone", userDetails.getUser().getPhone());
        profile.put("email", userDetails.getUser().getEmail());

        return ResponseEntity.ok(profile);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie(tokenProvider.getCookieName(), "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công"));
    }
}
