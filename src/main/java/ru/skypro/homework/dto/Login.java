package ru.skypro.homework.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.skypro.homework.constant.ApiConstants;

/**
 * DTO запроса авторизации.
 * <p>
 * Соответствует схеме {@code Login} из {@code openapi.yaml}. Используется в {@code POST /login}.
 */
@Data
@Schema(name = "Login", description = "Данные для авторизации пользователя")
public class Login {

    @NotBlank
    @Size(min = ApiConstants.PASSWORD_MIN_LENGTH, max = ApiConstants.PASSWORD_MAX_LENGTH)
    @Schema(description = "пароль", minLength = ApiConstants.PASSWORD_MIN_LENGTH,
            maxLength = ApiConstants.PASSWORD_MAX_LENGTH)
    private String password;

    @NotBlank
    @Size(min = ApiConstants.USERNAME_MIN_LENGTH, max = ApiConstants.USERNAME_MAX_LENGTH)
    @Schema(description = "логин", minLength = ApiConstants.USERNAME_MIN_LENGTH,
            maxLength = ApiConstants.USERNAME_MAX_LENGTH)
    private String username;
}
