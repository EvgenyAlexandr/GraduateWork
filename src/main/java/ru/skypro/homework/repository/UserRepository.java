package ru.skypro.homework.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.skypro.homework.entity.User;

/**
 * Репозиторий для работы с пользователями.
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Ищет пользователя по email (логину).
     *
     * @param email адрес электронной почты
     */
    Optional<User> findByEmail(String email);

    /**
     * Проверяет уникальность email при регистрации.
     *
     * @param email адрес электронной почты
     */
    boolean existsByEmail(String email);
}
