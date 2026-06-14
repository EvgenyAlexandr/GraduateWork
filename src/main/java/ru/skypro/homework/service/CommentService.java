package ru.skypro.homework.service;

import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;

/**
 * Контракт сервиса комментариев: CRUD, маппинг Entity ↔ DTO и проверка прав доступа.
 */
public interface CommentService {

    /**
     * Возвращает комментарии объявления; если объявление не найдено — 404.
     *
     * @param adId первичный ключ объявления
     */
    Comments getComments(Integer adId);

    /**
     * Добавляет комментарий от пользователя с указанным email (логином).
     *
     * @param adId      первичный ключ объявления
     * @param comment   текст комментария
     * @param userEmail email автора из Basic Auth
     * @return DTO созданного комментария
     */
    Comment addComment(Integer adId, CreateOrUpdateComment comment, String userEmail);

    /**
     * Обновляет комментарий с проверкой прав владельца или ADMIN.
     *
     * @param adId      первичный ключ объявления
     * @param commentId первичный ключ комментария
     * @param comment   новый текст
     * @param userEmail email текущего пользователя
     * @return DTO обновлённого комментария
     */
    Comment updateComment(Integer adId, Integer commentId, CreateOrUpdateComment comment, String userEmail);

    /**
     * Удаляет комментарий с проверкой прав владельца или ADMIN.
     *
     * @param adId      первичный ключ объявления
     * @param commentId первичный ключ комментария
     * @param userEmail email текущего пользователя
     */
    void deleteComment(Integer adId, Integer commentId, String userEmail);

    /**
     * Создаёт комментарий (используется в тестах и внутренней логике).
     *
     * @param createOrUpdateComment текст комментария
     * @param ad                    объявление
     * @param author                автор
     * @return сохранённая сущность
     */
    ru.skypro.homework.entity.Comment createComment(
            CreateOrUpdateComment createOrUpdateComment, Ad ad, User author);

    /**
     * Преобразует сущность в DTO для ответа API.
     */
    Comment toDto(ru.skypro.homework.entity.Comment comment);
}
