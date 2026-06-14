package ru.skypro.homework.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ru.skypro.homework.constant.ApiConstants;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.security.SecurityUtils;
import ru.skypro.homework.service.AdService;

/**
 * REST-контроллер объявлений.
 * <p>
 * Базовый путь: {@code /ads}. Комментарии вынесены в {@link CommentsController}.
 */
@CrossOrigin(origins = ApiConstants.CORS_ORIGIN)
@RestController
@RequestMapping("/ads")
@Tag(name = "Объявления")
@RequiredArgsConstructor
public class AdsController {

    private final AdService adService;
    private final SecurityUtils securityUtils;

    /**
     * Возвращает список всех объявлений на платформе (доступ без авторизации).
     */
    @Operation(summary = "Получение всех объявлений", operationId = "getAllAds")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ads.class)))
    @GetMapping
    public ResponseEntity<Ads> getAllAds() {
        return ResponseEntity.ok(adService.getAllAds());
    }

    /**
     * Создаёт новое объявление от имени текущего пользователя и сохраняет изображение на диск.
     */
    @Operation(summary = "Добавление объявления", operationId = "addAd")
    @ApiResponse(responseCode = "201", description = "Created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ad.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Ad> addAd(
            @Valid @RequestPart("properties") CreateOrUpdateAd properties,
            @RequestPart("image") MultipartFile image,
            Authentication authentication) {
        ru.skypro.homework.entity.User author = securityUtils.getCurrentUser(authentication);
        ru.skypro.homework.entity.Ad ad = adService.createAd(properties, author, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(adService.toDto(ad));
    }

    /**
     * Возвращает объявления текущего авторизованного пользователя.
     */
    @Operation(summary = "Получение объявлений авторизованного пользователя", operationId = "getAdsMe")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ads.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/me")
    public ResponseEntity<Ads> getAdsMe(Authentication authentication) {
        ru.skypro.homework.entity.User currentUser = securityUtils.getCurrentUser(authentication);
        return ResponseEntity.ok(adService.getAdsByAuthorId(currentUser.getId()));
    }

    /**
     * Возвращает расширенную информацию об объявлении по идентификатору.
     */
    @Operation(summary = "Получение информации об объявлении", operationId = "getAds")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExtendedAd.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAd> getAd(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(adService.getExtendedAd(id));
    }

    /**
     * Удаляет объявление с проверкой прав владельца или ADMIN.
     */
    @Operation(summary = "Удаление объявления", operationId = "removeAd")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeAd(@PathVariable("id") Integer id, Authentication authentication) {
        ru.skypro.homework.entity.User currentUser = securityUtils.getCurrentUser(authentication);
        adService.deleteAd(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновляет текстовые поля объявления с проверкой прав.
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
            @Valid @RequestBody CreateOrUpdateAd createOrUpdateAd,
            Authentication authentication) {
        ru.skypro.homework.entity.User currentUser = securityUtils.getCurrentUser(authentication);
        ru.skypro.homework.entity.Ad ad = adService.updateAd(id, createOrUpdateAd, currentUser);
        return ResponseEntity.ok(adService.toDto(ad));
    }

    /**
     * Заменяет изображение объявления и возвращает байты сохранённого файла.
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
            @RequestPart("image") MultipartFile image,
            Authentication authentication) {
        ru.skypro.homework.entity.User currentUser = securityUtils.getCurrentUser(authentication);
        byte[] imageBytes = adService.updateAdImage(id, image, currentUser);
        return ResponseEntity.ok(imageBytes);
    }
}
