package com.example.security_learning.security;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.security_learning.service.CustomUserDetailsService;
import com.example.security_learning.service.RefreshTokenService;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Central configuration for Spring Security.
 *
 * <p>Configures authentication, authorization, CSRF, CORS, session
 * management, remember-me authentication, JWT authentication, and
 * security-related HTTP headers.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Configures the application's Spring Security filter chain.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider,
            RememberMeServices rememberMeServices,
            RefreshTokenService refreshTokenService)
            throws Exception {

        http
            .cors(cors -> {})
            .csrf(csrf -> csrf
                .csrfTokenRepository(
                    CookieCsrfTokenRepository.withHttpOnlyFalse()
                )
            )
            .headers(headers -> headers
                .referrerPolicy(referrer -> referrer
                    .policy(ReferrerPolicy.SAME_ORIGIN)
                )
            )
            .authorizeHttpRequests(auth -> auth

                // Public authentication and account endpoints
                .requestMatchers(
                    "/register",
                    "/login",
                    "/jwt-login",
                    "/csrf",
                    "/forgot-password",
                    "/reset-password",
                    "/refresh-token",
                    "/revoke-refresh-token"
                ).permitAll()

                // Role-based authorization
                .requestMatchers("/admin")
                .hasRole("ADMIN")

                .requestMatchers("/user")
                .hasAnyRole("USER", "ADMIN")

                // Permission-based authorization
                .requestMatchers("/users/read")
                .hasAuthority("USER_READ")
                
                

                // Product authorization
                .requestMatchers(HttpMethod.GET, "/products/**")
                .hasAnyRole("USER", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/products/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/products/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/products/**")
                .hasRole("ADMIN")

                // Category authorization
                .requestMatchers(HttpMethod.GET, "/categories/**")
                .hasAnyRole("USER", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/categories/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/categories/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/categories/**")
                .hasRole("ADMIN")
                
                .requestMatchers(HttpMethod.GET, "/inventory/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/inventory/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/inventory/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/inventory/**").hasRole("ADMIN")
                
                .requestMatchers("/cart/**").authenticated()
                
                .requestMatchers(HttpMethod.GET, "/orders/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/orders/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/orders/*/status").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/payments/**").authenticated()

                .anyRequest()
                .authenticated()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new SecurityExceptionHandler())
                .accessDeniedHandler(new SecurityExceptionHandler())
            )
            .sessionManagement(session -> session
                .sessionFixation(fixation -> fixation.changeSessionId())
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutSuccessHandler((request, response, authentication) -> {
                    if (authentication != null) {
                        refreshTokenService.revokeAllRefreshTokensForUser(
                            authentication.getName()
                        );
                    }

                    response.setStatus(HttpServletResponse.SC_OK);
                })
                .permitAll()
            )
            .rememberMe(remember -> remember
                .rememberMeServices(rememberMeServices)
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .authenticationProvider(authenticationProvider);

        return http.build();
    }

    /**
     * Provides the password encoder used for user passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures username/password authentication using the application's
     * {@link CustomUserDetailsService}.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(
            CustomUserDetailsService customUserDetailsService) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(customUserDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    /**
     * Provides the application's authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    /**
     * Stores and tracks active user sessions.
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * Publishes HTTP session lifecycle events required by Spring Security
     * session management.
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /**
     * Persists the authenticated security context in the HTTP session.
     */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    /**
     * Configures session authentication strategies including concurrent
     * session control, session fixation protection, and session registration.
     */
    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy(
            SessionRegistry sessionRegistry) {

        ConcurrentSessionControlAuthenticationStrategy concurrent =
                new ConcurrentSessionControlAuthenticationStrategy(
                    sessionRegistry
                );

        concurrent.setMaximumSessions(10);
        concurrent.setExceptionIfMaximumExceeded(false);

        return new CompositeSessionAuthenticationStrategy(
            List.of(
                concurrent,
                new ChangeSessionIdAuthenticationStrategy(),
                new RegisterSessionAuthenticationStrategy(sessionRegistry)
            )
        );
    }

    /**
     * Configures allowed cross-origin requests for the application.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${cors.allowed-origins}") String allowedOrigins) {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
            Arrays.stream(allowedOrigins.split(","))
                    .map(String::trim)
                    .filter(origin -> !origin.isBlank())
                    .toList()
        );

        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(List.of("*"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
    /**
     * Creates the JWT decoder used to validate HMAC-SHA256 signed tokens.
     */
    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${jwt.secret}") String secret) {

        SecretKeySpec secretKey =
                new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
                );

        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    /**
     * Converts JWT role and permission claims into Spring Security authorities.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> authorities =
                    new java.util.ArrayList<>();

            String role = jwt.getClaimAsString("role");

            if (role != null) {
                authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + role)
                );
            }

            List<String> permissions =
                    jwt.getClaimAsStringList("permissions");

            if (permissions != null) {
                permissions.forEach(permission ->
                    authorities.add(
                        new SimpleGrantedAuthority(permission)
                    )
                );
            }

            return authorities;
        });

        return converter;
    }

    /**
     * Configures remember-me authentication for session-based login.
     */
    @Bean
    public RememberMeServices rememberMeServices(
            CustomUserDetailsService customUserDetailsService,
            @Value("${remember-me.key}") String rememberMeKey) {

        if (rememberMeKey == null || rememberMeKey.isBlank()) {
            throw new IllegalStateException(
                    "REMEMBER_ME_KEY must be configured"
            );
        }

        if (rememberMeKey.length() < 32) {
            throw new IllegalStateException(
                    "REMEMBER_ME_KEY must be at least 32 characters long"
            );
        }

        TokenBasedRememberMeServices rememberMe =
                new TokenBasedRememberMeServices(
                    rememberMeKey,
                    customUserDetailsService
                );

        rememberMe.setTokenValiditySeconds(7 * 24 * 60 * 60);

        return rememberMe;
    }

    /**
     * Provides the authentication provider used to validate remember-me
     * authentication tokens.
     */
    @Bean
    public RememberMeAuthenticationProvider rememberMeAuthenticationProvider(
            @Value("${remember-me.key}") String rememberMeKey) {

        return new RememberMeAuthenticationProvider(rememberMeKey);
    }
}