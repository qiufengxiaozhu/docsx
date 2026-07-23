package com.docsx.security;

import com.docsx.mapper.AppMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final AppMapper appMapper;
    private final HmacVerifier hmacVerifier;

    public SecurityConfig(JwtUtils jwtUtils, AppMapper appMapper, HmacVerifier hmacVerifier) {
        this.jwtUtils = jwtUtils;
        this.appMapper = appMapper;
        this.hmacVerifier = hmacVerifier;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/compare/**").permitAll()
                .requestMatchers("/view/**").permitAll()
                .requestMatchers("/onlyoffice/**").permitAll()
                .requestMatchers("/onlyoffice-custom/**").permitAll()
                .requestMatchers("/admin/api/auth/login").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/admin/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .addFilterBefore(new JwtAuthFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new HmacAuthFilter(appMapper, hmacVerifier), JwtAuthFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
