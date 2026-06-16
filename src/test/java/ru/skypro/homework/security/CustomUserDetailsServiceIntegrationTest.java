package ru.skypro.homework.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import ru.skypro.homework.dto.Role;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.support.IntegrationTestData;

/**
 * Интеграционный тест {@link CustomUserDetailsService} с H2.
 * <p>
 * Подтверждает загрузку пользователя и роли из БД для Basic Auth.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CustomUserDetailsServiceIntegrationTest {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        IntegrationTestData.persistUser(
                userRepository, passwordEncoder,
                IntegrationTestData.USER_EMAIL, Role.USER, "Ivan", "Ivanov");
    }

    @Test
    @DisplayName("loadUserByUsername загружает пользователя и BCrypt-пароль из БД")
    void loadUserByUsername_loadsFromDatabase() {
        UserDetails details = customUserDetailsService.loadUserByUsername(IntegrationTestData.USER_EMAIL);

        assertThat(details.getUsername()).isEqualTo(IntegrationTestData.USER_EMAIL);
        assertThat(passwordEncoder.matches(IntegrationTestData.PASSWORD, details.getPassword())).isTrue();
        assertThat(details.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }

    @Test
    @DisplayName("loadUserByUsername для неизвестного email выбрасывает UsernameNotFoundException")
    void loadUserByUsername_unknownEmail_throws() {
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("missing@test.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
