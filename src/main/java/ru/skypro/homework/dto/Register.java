package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.skypro.homework.constant.ApiConstants;

/**
 * DTO запроса регистрации нового пользователя.
 * <p>
 * Соответствует схеме {@code Register} из {@code openapi.yaml}. Используется в {@code POST /register}.
 */
@Data
@Schema(name = "Register", description = "Данные для регистрации пользователя")
public class Register {

    @Schema(description = "логин", minLength = ApiConstants.USERNAME_MIN_LENGTH,
            maxLength = ApiConstants.USERNAME_MAX_LENGTH)
    private String username;

    @Schema(description = "пароль", minLength = ApiConstants.PASSWORD_MIN_LENGTH,
            maxLength = ApiConstants.PASSWORD_MAX_LENGTH)
    private String password;

    @Schema(description = "имя пользователя", minLength = ApiConstants.FIRST_NAME_MIN_LENGTH,
            maxLength = ApiConstants.FIRST_NAME_MAX_LENGTH)
    private String firstName;

    @Schema(description = "фамилия пользователя", minLength = ApiConstants.LAST_NAME_MIN_LENGTH,
            maxLength = ApiConstants.LAST_NAME_MAX_LENGTH)
    private String lastName;

    @Schema(description = "телефон пользователя", pattern = ApiConstants.PHONE_PATTERN)
    private String phone;

    @Schema(description = "роль пользователя", implementation = Role.class)
    private Role role;
}
