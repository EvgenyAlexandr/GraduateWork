package ru.skypro.homework.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;

/**
 * Сервис пользователей: маппинг Entity ↔ DTO и работа с репозиторием.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Возвращает DTO пользователя по идентификатору.
     *
     * @param id первичный ключ пользователя
     */
    public Optional<ru.skypro.homework.dto.User> getUserDto(Integer id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    /**
     * Возвращает DTO пользователя по email (логину).
     *
     * @param email адрес электронной почты
     */
    public Optional<ru.skypro.homework.dto.User> getUserDtoByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toDto);
    }

    /**
     * Ищет сущность пользователя по email для внутренней бизнес-логики.
     *
     * @param email адрес электронной почты
     */
    public Optional<User> findEntityByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Ищет сущность пользователя по идентификатору.
     *
     * @param id первичный ключ пользователя
     */
    public Optional<User> findEntityById(Integer id) {
        return userRepository.findById(id);
    }

    /**
     * Проверяет, занят ли email при регистрации.
     *
     * @param email адрес электронной почты
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Создаёт нового пользователя из данных регистрации.
     *
     * @param register данные из тела запроса {@code POST /register}
     * @return сохранённая сущность
     */
    @Transactional
    public User createUser(Register register) {
        User user = userMapper.toEntity(register);
        return userRepository.save(user);
    }

    /**
     * Обновляет профиль пользователя (имя, фамилия, телефон).
     *
     * @param id         первичный ключ пользователя
     * @param updateUser новые значения полей профиля
     * @return обновлённая сущность или пустой {@link Optional}, если пользователь не найден
     */
    @Transactional
    public Optional<User> updateUser(Integer id, UpdateUser updateUser) {
        return userRepository.findById(id).map(user -> {
            userMapper.updateEntityFromDto(updateUser, user);
            return userRepository.save(user);
        });
    }

    /**
     * Сохраняет или обновляет сущность пользователя (например, после смены пароля или аватара).
     */
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Преобразует сущность в DTO для ответа API.
     */
    public ru.skypro.homework.dto.User toDto(User user) {
        return userMapper.toDto(user);
    }

    /**
     * Формирует DTO обновления профиля из текущих данных сущности.
     */
    public UpdateUser toUpdateUserDto(User user) {
        UpdateUser updateUser = new UpdateUser();
        updateUser.setFirstName(user.getFirstName());
        updateUser.setLastName(user.getLastName());
        updateUser.setPhone(user.getPhone());
        return updateUser;
    }
}
