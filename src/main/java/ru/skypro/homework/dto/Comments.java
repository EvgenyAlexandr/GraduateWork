package ru.skypro.homework.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO списка комментариев к объявлению.
 * <p>
 * Соответствует схеме {@code Comments} из {@code openapi.yaml}.
 */
@Data
@Schema(name = "Comments", description = "Список комментариев")
public class Comments {

    @Schema(description = "общее количество комментариев")
    private Integer count;

    @Schema(description = "список комментариев")
    private List<Comment> results;
}
