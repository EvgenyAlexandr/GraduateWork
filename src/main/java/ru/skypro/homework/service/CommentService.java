package ru.skypro.homework.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.CommentRepository;

/**
 * Сервис комментариев: маппинг Entity ↔ DTO и работа с репозиторием.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    /**
     * Возвращает все комментарии к объявлению в формате {@link Comments}.
     *
     * @param adId первичный ключ объявления
     */
    public Comments getCommentsByAdId(Integer adId) {
        List<ru.skypro.homework.dto.Comment> results = commentRepository.findAllByAd_Pk(adId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
        Comments comments = new Comments();
        comments.setCount(results.size());
        comments.setResults(results);
        return comments;
    }

    /**
     * Возвращает DTO комментария по идентификатору.
     *
     * @param commentId первичный ключ комментария
     */
    public Optional<ru.skypro.homework.dto.Comment> getCommentDto(Integer commentId) {
        return commentRepository.findById(commentId).map(commentMapper::toDto);
    }

    /**
     * Создаёт комментарий к объявлению от имени указанного автора.
     *
     * @param createOrUpdateComment текст комментария
     * @param ad                    объявление, к которому добавляется комментарий
     * @param author                пользователь-автор комментария
     * @return сохранённая сущность с проставленной датой создания
     */
    @Transactional
    public Comment createComment(CreateOrUpdateComment createOrUpdateComment, Ad ad, User author) {
        Comment comment = commentMapper.toEntity(createOrUpdateComment);
        comment.setAd(ad);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    /**
     * Обновляет текст комментария по идентификатору.
     *
     * @param commentId             первичный ключ комментария
     * @param createOrUpdateComment новый текст
     * @return обновлённая сущность или пустой {@link Optional}, если комментарий не найден
     */
    @Transactional
    public Optional<Comment> updateComment(Integer commentId, CreateOrUpdateComment createOrUpdateComment) {
        return commentRepository.findById(commentId).map(comment -> {
            commentMapper.updateEntityFromDto(createOrUpdateComment, comment);
            return commentRepository.save(comment);
        });
    }

    /**
     * Удаляет комментарий по идентификатору.
     *
     * @param commentId первичный ключ комментария
     * @return {@code true}, если комментарий был удалён; {@code false}, если не найден
     */
    @Transactional
    public boolean deleteComment(Integer commentId) {
        if (!commentRepository.existsById(commentId)) {
            return false;
        }
        commentRepository.deleteById(commentId);
        return true;
    }

    /**
     * Преобразует сущность в DTO для ответа API.
     */
    public ru.skypro.homework.dto.Comment toDto(Comment comment) {
        return commentMapper.toDto(comment);
    }
}
