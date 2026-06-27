package ru.skypro.homework.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.service.UserService;

/**
 * Unit-тесты {@link AuthServiceImpl} с моками {@link UserService} и {@link PasswordEncoder}.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("login возвращает true при совпадении пароля с BCrypt-хешем в БД")
    void login_returnsTrueWhenPasswordMatches() {
        User user = new User();
        user.setPassword("encoded");
        when(userService.findEntityByEmail("user@test.com")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("secret12", "encoded")).thenReturn(true);

        assertThat(authService.login("user@test.com", "secret12")).isTrue();
    }

    @Test
    @DisplayName("login возвращает false, если пользователь не найден в БД")
    void login_returnsFalseWhenUserMissing() {
        when(userService.findEntityByEmail("missing@test.com")).thenReturn(java.util.Optional.empty());

        assertThat(authService.login("missing@test.com", "secret12")).isFalse();
    }

    @Test
    @DisplayName("register возвращает false, если email уже занят")
    void register_returnsFalseWhenEmailExists() {
        Register register = new Register();
        register.setUsername("user@test.com");
        when(userService.existsByEmail("user@test.com")).thenReturn(true);

        assertThat(authService.register(register)).isFalse();
        verify(userService, never()).createUser(any());
    }

    @Test
    @DisplayName("register создаёт пользователя в БД при свободном email")
    void register_createsUserWhenEmailFree() {
        Register register = new Register();
        register.setUsername("new@test.com");
        register.setPassword("password1");
        register.setRole(Role.USER);
        when(userService.existsByEmail("new@test.com")).thenReturn(false);

        assertThat(authService.register(register)).isTrue();
        verify(userService).createUser(register);
    }

    @Test
    @DisplayName("changePassword делегирует проверку и сохранение хеша в UserService")
    void changePassword_delegatesToUserService() {
        when(userService.changePassword("user@test.com", "oldPass12", "newPass123")).thenReturn(true);

        assertThat(authService.changePassword("user@test.com", "oldPass12", "newPass123")).isTrue();
    }
}
