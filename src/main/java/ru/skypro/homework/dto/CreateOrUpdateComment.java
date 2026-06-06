package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.skypro.homework.constant.ApiConstants;

/**
 * DTO создания или обновления комментария.
 * <p>
 * Соответствует схеме {@code CreateOrUpdateComment} из {@code openapi.yaml}.
 */
@Data
@Schema(name = "CreateOrUpdateComment", description = "Данные для создания или обновления комментария")
public class CreateOrUpdateComment {

    @Schema(description = "текст комментария", requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = ApiConstants.COMMENT_TEXT_MIN_LENGTH,
            maxLength = ApiConstants.COMMENT_TEXT_MAX_LENGTH)
    private String text;
}
