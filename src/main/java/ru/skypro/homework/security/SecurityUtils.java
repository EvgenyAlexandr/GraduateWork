package ru.skypro.homework.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.service.UserService;

/**
 * Утилиты для получения текущего пользователя из Security-контекста.
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserService userService;

    /**
     * Возвращает сущность пользователя, прошедшего Basic Auth.
     *
     * @param authentication объект аутентификации Spring Security
     * @return пользователь из БД
     * @throws BadCredentialsException если пользователь не найден
     */
    public User getCurrentUser(Authentication authentication) {
        return userService.findEntityByEmail(authentication.getName())
                .orElseThrow(() -> new BadCredentialsException("Пользователь не найден"));
    }
}
