package ru.skypro.homework.controller;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.security.SecurityUtils;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.service.UserService;

/**
 * REST-контроллер операций с профилем авторизованного пользователя.
 * <p>
 * Базовый путь: {@code /users}. Данные текущего пользователя берутся из Basic Auth.
 */
@CrossOrigin(origins = ApiConstants.CORS_ORIGIN)
@RestController
@RequestMapping("/users")
@Tag(name = "Пользователи")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final UserService userService;
    private final SecurityUtils securityUtils;

    /**
     * Меняет пароль текущего пользователя после проверки текущего пароля.
     *
     * @param newPassword    текущий и новый пароль
     * @param authentication данные Basic Auth
     * @return {@code 200 OK} при успехе; {@code 400} при неверном текущем пароле
     */
    @Operation(summary = "Обновление пароля", operationId = "setPassword")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PostMapping("/set_password")
    public ResponseEntity<Void> setPassword(@Valid @RequestBody NewPassword newPassword,
            Authentication authentication) {
        boolean changed = authService.changePassword(
                authentication.getName(),
                newPassword.getCurrentPassword(),
                newPassword.getNewPassword());
        if (!changed) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Возвращает данные текущего авторизованного пользователя.
     *
     * @param authentication данные Basic Auth
     * @return DTO пользователя из БД
     */
    @Operation(summary = "Получение информации об авторизованном пользователе", operationId = "getUser")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/me")
    public ResponseEntity<User> getUser(Authentication authentication) {
        ru.skypro.homework.entity.User currentUser = securityUtils.getCurrentUser(authentication);
        return ResponseEntity.ok(userService.toDto(currentUser));
    }

    /**
     * Обновляет имя, фамилию и телефон текущего пользователя.
     *
     * @param updateUser     новые данные профиля
     * @param authentication данные Basic Auth
     * @return обновлённый профиль
     */
    @Operation(summary = "Обновление информации об авторизованном пользователе", operationId = "updateUser")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UpdateUser.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PatchMapping("/me")
    public ResponseEntity<UpdateUser> updateUser(@RequestBody UpdateUser updateUser,
            Authentication authentication) {
        ru.skypro.homework.entity.User currentUser = securityUtils.getCurrentUser(authentication);
        return userService.updateUser(currentUser.getId(), updateUser)
                .map(user -> ResponseEntity.ok(userService.toUpdateUserDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Загружает новый аватар текущего пользователя и сохраняет файл на диск.
     *
     * @param image          файл изображения (multipart/form-data)
     * @param authentication данные Basic Auth
     * @return {@code 200 OK} при успешном сохранении
     */
    @Operation(summary = "Обновление аватара авторизованного пользователя", operationId = "updateUserImage")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> updateUserImage(@RequestPart("image") MultipartFile image,
            Authentication authentication) {
        ru.skypro.homework.entity.User currentUser = securityUtils.getCurrentUser(authentication);
        ru.skypro.homework.entity.User updated = userService.updateUserImage(currentUser.getId(), image);
        return ResponseEntity.ok(userService.toDto(updated));
    }
}
