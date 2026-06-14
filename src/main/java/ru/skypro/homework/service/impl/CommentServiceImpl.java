package ru.skypro.homework.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.ResourceNotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.security.AccessChecker;
import ru.skypro.homework.service.CommentService;

/**
 * Реализация {@link CommentService}: CRUD комментариев через {@link CommentRepository}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final AccessChecker accessChecker;

    /** {@inheritDoc} */
    @Override
    public Comments getComments(Integer adId) {
        requireAdExists(adId);
        List<Comment> results = commentRepository.findAllByAd_Pk(adId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
        Comments comments = new Comments();
        comments.setCount(results.size());
        comments.setResults(results);
        return comments;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Comment addComment(Integer adId, CreateOrUpdateComment commentDto, String userEmail) {
        Ad ad = findAdOrThrow(adId);
        User author = findUserByEmailOrThrow(userEmail);
        return toDto(createComment(commentDto, ad, author));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Comment updateComment(Integer adId, Integer commentId,
            CreateOrUpdateComment commentDto, String userEmail) {
        User currentUser = findUserByEmailOrThrow(userEmail);
        ru.skypro.homework.entity.Comment comment = findCommentInAd(commentId, adId);
        accessChecker.checkOwnerOrAdmin(comment.getAuthor(), currentUser);
        commentMapper.updateEntityFromDto(commentDto, comment);
        return toDto(commentRepository.save(comment));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void deleteComment(Integer adId, Integer commentId, String userEmail) {
        User currentUser = findUserByEmailOrThrow(userEmail);
        ru.skypro.homework.entity.Comment comment = findCommentInAd(commentId, adId);
        accessChecker.checkOwnerOrAdmin(comment.getAuthor(), currentUser);
        commentRepository.delete(comment);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public ru.skypro.homework.entity.Comment createComment(
            CreateOrUpdateComment createOrUpdateComment, Ad ad, User author) {
        ru.skypro.homework.entity.Comment comment = commentMapper.toEntity(createOrUpdateComment);
        comment.setAd(ad);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    /** {@inheritDoc} */
    @Override
    public Comment toDto(ru.skypro.homework.entity.Comment comment) {
        return commentMapper.toDto(comment);
    }

    private void requireAdExists(Integer adId) {
        if (!adRepository.existsById(adId)) {
            throw new ResourceNotFoundException("Объявление не найдено: " + adId);
        }
    }

    private Ad findAdOrThrow(Integer adId) {
        return adRepository.findById(adId)
                .orElseThrow(() -> new ResourceNotFoundException("Объявление не найдено: " + adId));
    }

    private User findUserByEmailOrThrow(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadCredentialsException("Пользователь не найден: " + userEmail));
    }

    private ru.skypro.homework.entity.Comment findCommentInAd(Integer commentId, Integer adId) {
        return commentRepository.findByPkAndAd_Pk(commentId, adId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Комментарий " + commentId + " не найден в объявлении " + adId));
    }
}
