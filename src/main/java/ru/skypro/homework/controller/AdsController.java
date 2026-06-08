package ru.skypro.homework.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.skypro.homework.constant.ApiConstants;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.util.DefaultDtoFactory;

/**
 * REST-контроллер объявлений и комментариев к ним.
 * <p>
 * Базовый путь: {@code /ads}. На Этапе I возвращает заглушки без обращения к сервисам.
 */
@CrossOrigin(origins = ApiConstants.CORS_ORIGIN)
@RestController
@RequestMapping("/ads")
@Tag(name = "Объявления")
public class AdsController {

    /**
     * Возвращает список всех объявлений на платформе.
     *
     * @return пустой {@link Ads} с {@code count = 0} (Этап I)
     */
    @Operation(summary = "Получение всех объявлений", operationId = "getAllAds")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ads.class)))
    @GetMapping
    public ResponseEntity<Ads> getAllAds() {
        return ResponseEntity.ok(DefaultDtoFactory.emptyAds());
    }

    /**
     * Создаёт новое объявление с изображением.
     *
     * @param properties заголовок, цена и описание объявления
     * @param image      файл изображения
     * @return пустой {@link Ad} и статус {@code 201 Created} (Этап I)
     */
    @Operation(summary = "Добавление объявления", operationId = "addAd")
    @ApiResponse(responseCode = "201", description = "Created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ad.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Ad> addAd(
            @RequestPart("properties") CreateOrUpdateAd properties,
            @RequestPart("image") MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED).body(DefaultDtoFactory.emptyAd());
    }

    /**
     * Возвращает объявления текущего авторизованного пользователя.
     *
     * @return пустой {@link Ads} (Этап I)
     */
    @Operation(summary = "Получение объявлений авторизованного пользователя", operationId = "getAdsMe")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ads.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/me")
    public ResponseEntity<Ads> getAdsMe() {
        return ResponseEntity.ok(DefaultDtoFactory.emptyAds());
    }

    /**
     * Возвращает расширенную информацию об объявлении по идентификатору.
     *
     * @param id идентификатор объявления
     * @return пустой {@link ExtendedAd} (Этап I)
     */
    @Operation(summary = "Получение информации об объявлении", operationId = "getAds")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExtendedAd.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAd> getAd(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(DefaultDtoFactory.emptyExtendedAd());
    }

    /**
     * Удаляет объявление по идентификатору.
     *
     * @param id идентификатор объявления
     * @return {@code 204 No Content} при успешном удалении
     */
    @Operation(summary = "Удаление объявления", operationId = "removeAd")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeAd(@PathVariable("id") Integer id) {
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновляет текстовые поля объявления.
     *
     * @param id               идентификатор объявления
     * @param createOrUpdateAd новые данные объявления
     * @return пустой {@link Ad} (Этап I)
     */
    @Operation(summary = "Обновление информации об объявлении", operationId = "updateAds")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ad.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PatchMapping("/{id}")
    public ResponseEntity<Ad> updateAd(
            @PathVariable("id") Integer id,
            @RequestBody CreateOrUpdateAd createOrUpdateAd) {
        return ResponseEntity.ok(DefaultDtoFactory.emptyAd());
    }

    /**
     * Заменяет изображение объявления.
     *
     * @param id    идентификатор объявления
     * @param image новый файл изображения
     * @return пустой массив байт (Этап I)
     */
    @Operation(summary = "Обновление картинки объявления", operationId = "updateImage")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    schema = @Schema(type = "array", format = "byte")))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> updateImage(
            @PathVariable("id") Integer id,
            @RequestPart("image") MultipartFile image) {
        return ResponseEntity.ok(ApiConstants.EMPTY_BYTE_ARRAY);
    }

    /**
     * Возвращает комментарии к объявлению.
     *
     * @param id идентификатор объявления
     * @return пустой {@link Comments} (Этап I)
     */
    @Operation(summary = "Получение комментариев объявления", operationId = "getComments", tags = {"Комментарии"})
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Comments.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}/comments")
    public ResponseEntity<Comments> getComments(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(DefaultDtoFactory.emptyComments());
    }

    /**
     * Добавляет комментарий к объявлению.
     *
     * @param id                    идентификатор объявления
     * @param createOrUpdateComment текст комментария
     * @return пустой {@link Comment} (Этап I)
     */
    @Operation(summary = "Добавление комментария к объявлению", operationId = "addComment", tags = {"Комментарии"})
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Comment.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable("id") Integer id,
            @RequestBody CreateOrUpdateComment createOrUpdateComment) {
        return ResponseEntity.ok(DefaultDtoFactory.emptyComment());
    }

    /**
     * Удаляет комментарий под объявлением.
     *
     * @param adId      идентификатор объявления
     * @param commentId идентификатор комментария
     * @return {@code 200 OK} при успешном удалении
     */
    @Operation(summary = "Удаление комментария", operationId = "deleteComment", tags = {"Комментарии"})
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("adId") Integer adId,
            @PathVariable("commentId") Integer commentId) {
        return ResponseEntity.ok().build();
    }

    /**
     * Обновляет текст комментария.
     *
     * @param adId                  идентификатор объявления
     * @param commentId             идентификатор комментария
     * @param createOrUpdateComment новый текст комментария
     * @return пустой {@link Comment} (Этап I)
     */
    @Operation(summary = "Обновление комментария", operationId = "updateComment", tags = {"Комментарии"})
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Comment.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable("adId") Integer adId,
            @PathVariable("commentId") Integer commentId,
            @RequestBody CreateOrUpdateComment createOrUpdateComment) {
        return ResponseEntity.ok(DefaultDtoFactory.emptyComment());
    }
}
