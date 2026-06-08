package ru.skypro.homework.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.skypro.homework.entity.Ad;

/**
 * Репозиторий для работы с объявлениями.
 */
public interface AdRepository extends JpaRepository<Ad, Integer> {

    /**
     * Возвращает все объявления указанного автора.
     *
     * @param authorId идентификатор пользователя
     */
    List<Ad> findAllByAuthor_Id(Integer authorId);
}
