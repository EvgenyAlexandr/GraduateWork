package ru.skypro.homework.service;

import ru.skypro.homework.dto.Register;

/**
 * Контракт сервиса аутентификации и регистрации.
 * <p>
 * На текущем этапе работает с in-memory хранилищем Spring Security;
 * на Этапе III будет подключён к {@link UserService} и БД.
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
     * Регистрирует нового пользователя.
     *
     * @param register данные регистрации из тела запроса
     * @return {@code true}, если регистрация прошла успешно; {@code false}, если логин уже занят
     */
    boolean register(Register register);
}
