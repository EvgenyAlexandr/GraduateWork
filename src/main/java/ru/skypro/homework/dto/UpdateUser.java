package ru.skypro.homework.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.skypro.homework.constant.ApiConstants;

/**
 * DTO запроса обновления профиля пользователя.
 * <p>
 * Соответствует схеме {@code UpdateUser} из {@code openapi.yaml}. Используется в {@code PATCH /users/me}.
 */
@Data
@Schema(name = "UpdateUser", description = "Данные для обновления пользователя")
public class UpdateUser {

    @NotBlank
    @Size(min = ApiConstants.UPDATE_FIRST_NAME_MIN_LENGTH, max = ApiConstants.UPDATE_FIRST_NAME_MAX_LENGTH)
    @Schema(description = "имя пользователя", minLength = ApiConstants.UPDATE_FIRST_NAME_MIN_LENGTH,
            maxLength = ApiConstants.UPDATE_FIRST_NAME_MAX_LENGTH)
    private String firstName;

    @NotBlank
    @Size(min = ApiConstants.UPDATE_LAST_NAME_MIN_LENGTH, max = ApiConstants.UPDATE_LAST_NAME_MAX_LENGTH)
    @Schema(description = "фамилия пользователя", minLength = ApiConstants.UPDATE_LAST_NAME_MIN_LENGTH,
            maxLength = ApiConstants.UPDATE_LAST_NAME_MAX_LENGTH)
    private String lastName;

    @NotBlank
    @Pattern(regexp = ApiConstants.PHONE_PATTERN)
    @Schema(description = "телефон пользователя", pattern = ApiConstants.PHONE_PATTERN)
    private String phone;
}
