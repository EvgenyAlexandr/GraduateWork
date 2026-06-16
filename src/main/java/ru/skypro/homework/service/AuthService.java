package ru.skypro.homework.service;

import ru.skypro.homework.dto.Register;

/**
 * Контракт сервиса аутентификации и регистрации через БД.
 */
public interface AuthService {

    /**
     * Проверяет учётные данные пользователя.
     *
     * @param userName логин (email)
     * @param password пароль в открытом виде
     * @return {@code true}, если пользователь существует и пароль совпадает
     */
    boolean login(String userName, String password);

    /**
     * Регистрирует нового пользователя с BCrypt-хешем пароля.
     *
     * @param register данные регистрации из тела запроса
     * @return {@code true}, если регистрация прошла успешно; {@code false}, если логин уже занят
     */
    boolean register(Register register);

    /**
     * Меняет пароль авторизованного пользователя.
     *
     * @param email           email текущего пользователя
     * @param currentPassword текущий пароль
     * @param newPassword     новый пароль
     * @return {@code true} при успехе; {@code false} при неверном текущем пароле или отсутствии пользователя
     */
    boolean changePassword(String email, String currentPassword, String newPassword);
}
