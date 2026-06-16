package ru.skypro.homework.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.skypro.homework.entity.Comment;

/**
 * Репозиторий для работы с комментариями.
 */
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    /**
     * Возвращает все комментарии к объявлению.
     *
     * @param adPk первичный ключ объявления
     */
    List<Comment> findAllByAd_Pk(Integer adPk);

    /**
     * Ищет комментарий по id в рамках конкретного объявления.
     *
     * @param pk   первичный ключ комментария
     * @param adPk первичный ключ объявления
     */
    Optional<Comment> findByPkAndAd_Pk(Integer pk, Integer adPk);
}
