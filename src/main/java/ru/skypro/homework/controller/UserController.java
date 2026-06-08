package ru.skypro.homework.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.skypro.homework.constant.ApiConstants;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.util.DefaultDtoFactory;

/**
 * REST-контроллер операций с профилем авторизованного пользователя.
 * <p>
 * Базовый путь: {@code /users}. На Этапе I возвращает заглушки без обращения к сервисам.
 */
@CrossOrigin(origins = ApiConstants.CORS_ORIGIN)
@RestController
@RequestMapping("/users")
@Tag(name = "Пользователи")
public class UserController {

    /**
     * Меняет пароль текущего пользователя.
     *
     * @param newPassword текущий и новый пароль
     * @return {@code 200 OK} при успешной смене пароля
     */
    @Operation(summary = "Обновление пароля", operationId = "setPassword")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PostMapping("/set_password")
    public ResponseEntity<Void> setPassword(@RequestBody NewPassword newPassword) {
        return ResponseEntity.ok().build();
    }

    /**
     * Возвращает данные текущего авторизованного пользователя.
     *
     * @return пустой {@link User} (Этап I)
     */
    @Operation(summary = "Получение информации об авторизованном пользователе", operationId = "getUser")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/me")
    public ResponseEntity<User> getUser() {
        return ResponseEntity.ok(DefaultDtoFactory.emptyUser());
    }

    /**
     * Обновляет имя, фамилию и телефон текущего пользователя.
     *
     * @param updateUser новые данные профиля
     * @return пустой {@link UpdateUser} (Этап I)
     */
    @Operation(summary = "Обновление информации об авторизованном пользователе", operationId = "updateUser")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UpdateUser.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PatchMapping("/me")
    public ResponseEntity<UpdateUser> updateUser(@RequestBody UpdateUser updateUser) {
        return ResponseEntity.ok(DefaultDtoFactory.emptyUpdateUser());
    }

    /**
     * Загружает новый аватар текущего пользователя.
     *
     * @param image файл изображения (multipart/form-data)
     * @return {@code 200 OK} при успешной загрузке
     */
    @Operation(summary = "Обновление аватара авторизованного пользователя", operationId = "updateUserImage")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateUserImage(@RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok().build();
    }
}
