package ru.skypro.homework.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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

    @NotBlank
    @Size(min = ApiConstants.USERNAME_MIN_LENGTH, max = ApiConstants.USERNAME_MAX_LENGTH)
    @Schema(description = "логин", minLength = ApiConstants.USERNAME_MIN_LENGTH,
            maxLength = ApiConstants.USERNAME_MAX_LENGTH)
    private String username;

    @NotBlank
    @Size(min = ApiConstants.PASSWORD_MIN_LENGTH, max = ApiConstants.PASSWORD_MAX_LENGTH)
    @Schema(description = "пароль", minLength = ApiConstants.PASSWORD_MIN_LENGTH,
            maxLength = ApiConstants.PASSWORD_MAX_LENGTH)
    private String password;

    @NotBlank
    @Size(min = ApiConstants.FIRST_NAME_MIN_LENGTH, max = ApiConstants.FIRST_NAME_MAX_LENGTH)
    @Schema(description = "имя пользователя", minLength = ApiConstants.FIRST_NAME_MIN_LENGTH,
            maxLength = ApiConstants.FIRST_NAME_MAX_LENGTH)
    private String firstName;

    @NotBlank
    @Size(min = ApiConstants.LAST_NAME_MIN_LENGTH, max = ApiConstants.LAST_NAME_MAX_LENGTH)
    @Schema(description = "фамилия пользователя", minLength = ApiConstants.LAST_NAME_MIN_LENGTH,
            maxLength = ApiConstants.LAST_NAME_MAX_LENGTH)
    private String lastName;

    @NotBlank
    @Pattern(regexp = ApiConstants.PHONE_PATTERN)
    @Schema(description = "телефон пользователя", pattern = ApiConstants.PHONE_PATTERN)
    private String phone;

    @NotNull
    @Schema(description = "роль пользователя", implementation = Role.class)
    private Role role;
}
