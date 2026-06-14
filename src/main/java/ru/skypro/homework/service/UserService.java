package ru.skypro.homework.service;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.entity.User;

/**
 * Контракт сервиса пользователей: маппинг Entity ↔ DTO и работа с репозиторием.
 */
public interface UserService {

    /**
     * Возвращает DTO пользователя по идентификатору.
     */
    Optional<ru.skypro.homework.dto.User> getUserDto(Integer id);

    /**
     * Возвращает DTO пользователя по email (логину).
     */
    Optional<ru.skypro.homework.dto.User> getUserDtoByEmail(String email);

    /**
     * Ищет сущность пользователя по email.
     */
    Optional<User> findEntityByEmail(String email);

    /**
     * Ищет сущность пользователя по идентификатору.
     */
    Optional<User> findEntityById(Integer id);

    /**
     * Проверяет, занят ли email при регистрации.
     */
    boolean existsByEmail(String email);

    /**
     * Создаёт пользователя с BCrypt-хешем пароля.
     */
    User createUser(Register register);

    /**
     * Обновляет профиль пользователя (имя, фамилия, телефон).
     */
    Optional<User> updateUser(Integer id, UpdateUser updateUser);

    /**
     * Меняет пароль после проверки текущего.
     *
     * @return {@code false} при неверном текущем пароле или отсутствии пользователя
     */
    boolean changePassword(String email, String currentPassword, String newPassword);

    /**
     * Сохраняет или обновляет сущность пользователя.
     */
    User save(User user);

    /**
     * Преобразует сущность в DTO для ответа API.
     */
    ru.skypro.homework.dto.User toDto(User user);

    /**
     * Формирует DTO обновления профиля из текущих данных сущности.
     */
    UpdateUser toUpdateUserDto(User user);

    /**
     * Сохраняет новый аватар пользователя на диск и обновляет путь в БД.
     *
     * @param id    идентификатор пользователя
     * @param image файл изображения
     * @return обновлённая сущность
     */
    User updateUserImage(Integer id, MultipartFile image);
}
