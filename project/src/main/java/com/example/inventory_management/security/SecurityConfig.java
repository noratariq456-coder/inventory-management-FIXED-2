package com.example.inventory_management.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    // Angular dev server origins - override with an environment variable in real deployments.
    private static final List<String> ALLOWED_ORIGINS = List.of(
            "http://localhost:4200",
            "http://127.0.0.1:4200"
    );

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ALLOWED_ORIGINS);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .authorizeHttpRequests(auth -> auth

                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Actuator health/info exposed for the Podman/presentation checks
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                        // Login is the entry point for the Angular frontend - must be reachable unauthenticated
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/me").authenticated()

                        // GET
                        .requestMatchers(HttpMethod.GET, "/api/**")
                        .hasAnyAuthority("ADMIN", "STORE_MANAGER", "EMPLOYEE")

                        // POST
                        .requestMatchers(HttpMethod.POST, "/api/**")
                        .hasAnyAuthority("ADMIN", "STORE_MANAGER")

                        // PUT
                        .requestMatchers(HttpMethod.PUT, "/api/**")
                        .hasAnyAuthority("ADMIN", "STORE_MANAGER")

                        // PATCH
                        .requestMatchers(HttpMethod.PATCH, "/api/**")
                        .hasAnyAuthority("ADMIN", "STORE_MANAGER")

                        // DELETE
                        .requestMatchers(HttpMethod.DELETE, "/api/**")
                        .hasAuthority("ADMIN")

                        .anyRequest().authenticated()
                
                
                    )


                .formLogin(form -> form.permitAll())


                .httpBasic(basic -> {})

                .logout(logout -> logout.permitAll());

        return http.build();
    }
}
