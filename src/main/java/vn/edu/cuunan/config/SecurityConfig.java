package vn.edu.cuunan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vn.edu.cuunan.security.CustomAccessDeniedHandler;
import vn.edu.cuunan.security.JwtAuthenticationEntryPoint;
import vn.edu.cuunan.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint authenticationEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers(
                "/api/**",
                "/ws/**",
                "/h2-console/**",
                "/auth/**",
                "/sos",
                "/sos/**",
                "/donations",
                "/donations/**",
                "/rescuer/**",
                "/coordinator/**",
                "/admin/**"
            ))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .authorizeHttpRequests(auth -> auth
                // Công khai hoàn toàn (Public)
                .requestMatchers(
                    "/",
                    "/error",
                    "/map",
                    "/map/**",
                    "/alerts/**",
                    "/sos",
                    "/sos/**",
                    "/auth/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/uploads/**",
                    "/webjars/**",
                    "/h2-console/**",
                    "/ws/**",
                    "/api/auth/**",
                    "/api/sos",
                    "/api/sos/**",
                    "/api/upload/**",
                    "/feed",
                    "/feed/**",
                    "/api/feed/**",
                    "/donations",
                    "/donations/**",
                    "/api/donations",
                    "/api/donations/**",
                    "/api/chat",
                    "/api/chat/**"
                ).permitAll()

                // Cẩm nang chỉ dành cho người dân đã đăng nhập
                .requestMatchers("/guide/**").hasRole("CITIZEN")

                // Phân hệ Điều Phối Viên (Coordinator & Admin)
                .requestMatchers("/coordinator/**", "/api/coordinator/**").hasAnyRole("COORDINATOR", "ADMIN")

                // Phân hệ Đội Cứu Hộ (Rescuer & Admin)
                .requestMatchers("/rescuer/**", "/api/rescuer/**").hasAnyRole("RESCUER", "ADMIN")

                // Phân hệ Quản Trị Hệ Thống (Admin)
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")

                // Tất cả yêu cầu còn lại (Profile, Citizen cá nhân...) yêu cầu đăng nhập
                .anyRequest().authenticated()
            );

        // Gắn JwtAuthenticationFilter trước UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
