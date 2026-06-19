package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.skypro.homework.constant.ApiConstants;

/**
 * DTO запроса обновления профиля пользователя.
 * <p>
 * Соответствует схеме {@code UpdateUser} из {@code openapi.yaml}. Используется в {@code PATCH /users/me}.
 * Валидация выполняется в {@link ru.skypro.homework.service.impl.UserServiceImpl}, а не через {@code @Valid},
 * чтобы корректно обрабатывать частичное обновление и нормализацию телефона.
 */
@Data
@Schema(name = "UpdateUser", description = "Данные для обновления пользователя")
public class UpdateUser {

    @Schema(description = "имя пользователя", minLength = ApiConstants.UPDATE_FIRST_NAME_MIN_LENGTH,
            maxLength = ApiConstants.UPDATE_FIRST_NAME_MAX_LENGTH)
    private String firstName;

    @Schema(description = "фамилия пользователя", minLength = ApiConstants.UPDATE_LAST_NAME_MIN_LENGTH,
            maxLength = ApiConstants.UPDATE_LAST_NAME_MAX_LENGTH)
    private String lastName;

    @Schema(description = "телефон пользователя", pattern = ApiConstants.PHONE_PATTERN)
    private String phone;
}
