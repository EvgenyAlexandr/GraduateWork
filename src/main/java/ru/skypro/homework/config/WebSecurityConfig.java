package ru.skypro.homework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import ru.skypro.homework.filter.BasicAuthCorsFilter;
import ru.skypro.homework.security.CustomUserDetailsService;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Конфигурация Spring Security: Basic Auth, правила доступа к эндпоинтам.
 * <p>
 * Пользователи загружаются из PostgreSQL через {@link CustomUserDetailsService}.
 */
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final BasicAuthCorsFilter basicAuthCorsFilter;

    /** Пути Swagger, регистрации и статики изображений, доступные без аутентификации. */
    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/webjars/**",
            "/login",
            "/register",
            "/images/**"
    };

    /**
     * Настраивает цепочку фильтров: публичный список объявлений, роли для изменения данных.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authProvider)
            throws Exception {
        http.csrf()
                .disable()
                .authorizeHttpRequests(authorization -> authorization
                        .mvcMatchers(AUTH_WHITELIST).permitAll()
                        .mvcMatchers(HttpMethod.GET, "/ads").permitAll()
                        .mvcMatchers(HttpMethod.POST, "/ads/**").hasAnyRole("USER", "ADMIN")
                        .mvcMatchers(HttpMethod.PATCH, "/ads/**").hasAnyRole("USER", "ADMIN")
                        .mvcMatchers(HttpMethod.DELETE, "/ads/**").hasAnyRole("USER", "ADMIN")
                        .mvcMatchers("/users/**").authenticated()
                        .mvcMatchers(HttpMethod.GET, "/ads/**").authenticated()
                        .anyRequest().authenticated())
                .authenticationProvider(authProvider)
                .addFilterBefore(basicAuthCorsFilter, BasicAuthenticationFilter.class)
                .cors(withDefaults())
                .httpBasic(withDefaults());
        return http.build();
    }

    /**
     * Связывает {@link CustomUserDetailsService} с BCrypt для проверки паролей при Basic Auth.
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * Кодировщик паролей BCrypt для хранения и проверки учётных данных.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
