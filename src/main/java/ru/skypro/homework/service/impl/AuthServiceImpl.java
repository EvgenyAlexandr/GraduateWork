package ru.skypro.homework.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.service.UserService;

/**
 * Реализация {@link AuthService}: регистрация, вход и смена пароля через {@link UserService} и БД.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /** {@inheritDoc} */
    @Override
    public boolean login(String userName, String password) {
        return userService.findEntityByEmail(userName)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    /** {@inheritDoc} */
    @Override
    public boolean register(Register register) {
        if (userService.existsByEmail(register.getUsername())) {
            return false;
        }
        userService.createUser(register);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean changePassword(String email, String currentPassword, String newPassword) {
        return userService.changePassword(email, currentPassword, newPassword);
    }
}
