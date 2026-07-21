package com.salman.dentalsystem.security.config;

import com.salman.dentalsystem.security.filter.JwtAuthenticationFilter;
import com.salman.dentalsystem.security.handler.CustomAccessDeniedHandler;
import com.salman.dentalsystem.security.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // throws Exception əlavə edildi
        return http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:5173"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/auth/logout").hasAnyRole("DENTIST", "ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.GET, "/v1/users/me").hasAnyRole("ADMIN", "DENTIST", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.PUT, "/v1/users/me").hasAnyRole("ADMIN", "DENTIST", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.PATCH, "/v1/users/me/password").hasAnyRole("ADMIN", "DENTIST", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.PATCH, "/v1/users/*/reset-password").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/v1/users/*/activate").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/v1/users").hasAnyRole("ADMIN", "RECEPTIONIST", "DENTIST")
                        .requestMatchers(HttpMethod.GET, "/v1/users/*").hasAnyRole("ADMIN", "RECEPTIONIST", "DENTIST")
                        .requestMatchers(HttpMethod.PUT, "/v1/users/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/v1/users/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/v1/patients/count").hasAnyRole("DENTIST", "ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.POST, "/v1/patients/{id}/appointments").hasAnyRole("DENTIST", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/v1/patients/*/appointments").hasAnyRole("DENTIST", "ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.POST, "/v1/patients").hasAnyRole("DENTIST", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/v1/patients").hasAnyRole("DENTIST", "ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.GET, "/v1/patients/*").hasAnyRole("DENTIST", "ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.PUT, "/v1/patients/*").hasAnyRole("DENTIST", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/v1/patients/*/activate").hasAnyRole("DENTIST", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/v1/patients/*").hasAnyRole("DENTIST", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/v1/appointments/*/cancel").hasAnyRole("DENTIST", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/v1/appointments/*/activate").hasAnyRole("DENTIST", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/v1/appointments").hasAnyRole("DENTIST", "ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.GET, "/v1/appointments/*").hasAnyRole("DENTIST", "ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.PUT, "/v1/appointments/*").hasAnyRole("DENTIST", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/v1/xray/upload").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/xray/*").hasAnyRole("DENTIST", "ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.GET, "/v1/appointments/today").hasAnyRole("DENTIST", "ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.GET, "/v1/appointments/trash").hasAnyRole("DENTIST", "ADMIN", "RECEPTIONIST")
                        .anyRequest().authenticated())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception { // throws Exception əlavə edildi
        return config.getAuthenticationManager();
    }
}
