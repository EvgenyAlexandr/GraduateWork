package ru.skypro.homework.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.skypro.homework.entity.Comment;

/**
 * Репозиторий для работы с комментариями.
 */
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    /**
     * Возвращает все комментарии к объявлению, отсортированные по дате создания.
     *
     * @param adPk первичный ключ объявления
     */
    List<Comment> findAllByAd_Pk(Integer adPk);
}
