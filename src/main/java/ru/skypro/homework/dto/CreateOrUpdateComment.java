package ru.skypro.homework.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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

    @NotBlank
    @Size(min = ApiConstants.COMMENT_TEXT_MIN_LENGTH, max = ApiConstants.COMMENT_TEXT_MAX_LENGTH)
    @Schema(description = "текст комментария", requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = ApiConstants.COMMENT_TEXT_MIN_LENGTH,
            maxLength = ApiConstants.COMMENT_TEXT_MAX_LENGTH)
    private String text;
}
