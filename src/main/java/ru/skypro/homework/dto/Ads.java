package ru.skypro.homework.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO постраничного списка объявлений.
 * <p>
 * Соответствует схеме {@code Ads} из {@code openapi.yaml}.
 */
@Data
@Schema(name = "Ads", description = "Список объявлений")
public class Ads {

    @Schema(description = "общее количество объявлений")
    private Integer count;

    @Schema(description = "список объявлений")
    private List<Ad> results;
}
