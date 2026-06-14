package ru.skypro.homework.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ru.skypro.homework.constant.ApiConstants;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.service.CommentService;

/**
 * REST-контроллер комментариев к объявлениям.
 * <p>
 * Базовый путь: {@code /ads/{id}/comments}. Email автора берётся из Basic Auth.
 */
@CrossOrigin(origins = ApiConstants.CORS_ORIGIN)
@RestController
@RequestMapping("/ads")
@Tag(name = "Комментарии", description = "Управление комментариями к объявлениям")
@RequiredArgsConstructor
public class CommentsController {

    private final CommentService commentService;

    /**
     * Возвращает все комментарии к объявлению.
     *
     * @param id первичный ключ объявления
     */
    @Operation(summary = "Получение комментариев объявления", operationId = "getComments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Comments.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/{id}/comments")
    public ResponseEntity<Comments> getComments(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(commentService.getComments(id));
    }

    /**
     * Добавляет комментарий от имени текущего пользователя.
     *
     * @param id             первичный ключ объявления
     * @param comment        текст комментария
     * @param authentication данные Basic Auth
     */
    @Operation(summary = "Добавление комментария к объявлению", operationId = "addComment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable("id") Integer id,
            @Valid @RequestBody CreateOrUpdateComment comment,
            Authentication authentication) {
        Comment created = commentService.addComment(id, comment, authentication.getName());
        return ResponseEntity.ok(created);
    }

    /**
     * Удаляет комментарий с проверкой прав владельца или ADMIN.
     */
    @Operation(summary = "Удаление комментария", operationId = "deleteComment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("adId") Integer adId,
            @PathVariable("commentId") Integer commentId,
            Authentication authentication) {
        commentService.deleteComment(adId, commentId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Обновляет текст комментария с проверкой прав.
     */
    @Operation(summary = "Обновление комментария", operationId = "updateComment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable("adId") Integer adId,
            @PathVariable("commentId") Integer commentId,
            @Valid @RequestBody CreateOrUpdateComment comment,
            Authentication authentication) {
        Comment updated = commentService.updateComment(adId, commentId, comment, authentication.getName());
        return ResponseEntity.ok(updated);
    }
}
