package ru.skypro.homework.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.support.IntegrationTestData;

/**
 * Интеграционные тесты {@link UserService} / {@link ru.skypro.homework.service.impl.UserServiceImpl}.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        user = IntegrationTestData.persistUser(
                userRepository, passwordEncoder,
                IntegrationTestData.USER_EMAIL, Role.USER, "Ivan", "Ivanov");
    }

    @Test
    @DisplayName("createUser сохраняет BCrypt-хеш пароля, а не открытый текст")
    void createUser_storesEncodedPassword() {
        Register register = new Register();
        register.setUsername("encoded@test.com");
        register.setPassword("password1");
        register.setFirstName("Enc");
        register.setLastName("Odedov");
        register.setPhone(IntegrationTestData.PHONE);
        register.setRole(Role.USER);

        User created = userService.createUser(register);
        User fromDb = userRepository.findById(created.getId()).orElseThrow();

        assertThat(fromDb.getPassword()).isNotEqualTo("password1");
        assertThat(passwordEncoder.matches("password1", fromDb.getPassword())).isTrue();
    }

    @Test
    @DisplayName("changePassword возвращает false при неверном текущем пароле")
    void changePassword_wrongOldPassword_returnsFalse() {
        boolean changed = userService.changePassword(
                IntegrationTestData.USER_EMAIL, "wrongpass", "newpass12");

        assertThat(changed).isFalse();
    }

    @Test
    @DisplayName("changePassword возвращает true и сохраняет новый BCrypt-хеш")
    void changePassword_correctOldPassword_updatesHash() {
        boolean changed = userService.changePassword(
                IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD, "newpass12");

        assertThat(changed).isTrue();
        User fromDb = userRepository.findById(user.getId()).orElseThrow();
        assertThat(passwordEncoder.matches("newpass12", fromDb.getPassword())).isTrue();
    }
}
