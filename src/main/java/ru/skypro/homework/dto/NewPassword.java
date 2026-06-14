package ru.skypro.homework.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.skypro.homework.constant.ApiConstants;

/**
 * DTO запроса смены пароля авторизованного пользователя.
 * <p>
 * Соответствует схеме {@code NewPassword} из {@code openapi.yaml}.
 */
@Data
@Schema(name = "NewPassword", description = "Данные для смены пароля")
public class NewPassword {

    @NotBlank
    @Size(min = ApiConstants.PASSWORD_MIN_LENGTH, max = ApiConstants.PASSWORD_MAX_LENGTH)
    @Schema(description = "текущий пароль", minLength = ApiConstants.PASSWORD_MIN_LENGTH,
            maxLength = ApiConstants.PASSWORD_MAX_LENGTH)
    private String currentPassword;

    @NotBlank
    @Size(min = ApiConstants.PASSWORD_MIN_LENGTH, max = ApiConstants.PASSWORD_MAX_LENGTH)
    @Schema(description = "новый пароль", minLength = ApiConstants.PASSWORD_MIN_LENGTH,
            maxLength = ApiConstants.PASSWORD_MAX_LENGTH)
    private String newPassword;
}
