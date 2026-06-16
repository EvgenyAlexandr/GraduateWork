package ru.skypro.homework.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.skypro.homework.constant.ApiConstants;

/**
 * DTO создания или обновления объявления.
 * <p>
 * Соответствует схеме {@code CreateOrUpdateAd} из {@code openapi.yaml}.
 */
@Data
@Schema(name = "CreateOrUpdateAd", description = "Данные для создания или обновления объявления")
public class CreateOrUpdateAd {

    @NotBlank
    @Size(min = ApiConstants.AD_TITLE_MIN_LENGTH, max = ApiConstants.AD_TITLE_MAX_LENGTH)
    @Schema(description = "заголовок объявления", minLength = ApiConstants.AD_TITLE_MIN_LENGTH,
            maxLength = ApiConstants.AD_TITLE_MAX_LENGTH)
    private String title;

    @NotNull
    @Min(ApiConstants.AD_PRICE_MIN)
    @Max(ApiConstants.AD_PRICE_MAX)
    @Schema(description = "цена объявления", minimum = "" + ApiConstants.AD_PRICE_MIN,
            maximum = "" + ApiConstants.AD_PRICE_MAX)
    private Integer price;

    @NotBlank
    @Size(min = ApiConstants.AD_DESCRIPTION_MIN_LENGTH, max = ApiConstants.AD_DESCRIPTION_MAX_LENGTH)
    @Schema(description = "описание объявления", minLength = ApiConstants.AD_DESCRIPTION_MIN_LENGTH,
            maxLength = ApiConstants.AD_DESCRIPTION_MAX_LENGTH)
    private String description;
}
