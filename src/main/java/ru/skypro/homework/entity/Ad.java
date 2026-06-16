package ru.skypro.homework.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.skypro.homework.constant.ApiConstants;

/**
 * Сущность объявления о продаже.
 * <p>
 * Связана с автором через {@link #author}; изображение хранится как путь или URL.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ad")
public class Ad {

    /** Первичный ключ объявления (имя поля {@code pk} соответствует OpenAPI). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;

    @NotBlank
    @Size(min = ApiConstants.AD_TITLE_MIN_LENGTH, max = ApiConstants.AD_TITLE_MAX_LENGTH)
    @Column(nullable = false, length = ApiConstants.AD_TITLE_MAX_LENGTH)
    private String title;

    @NotBlank
    @Size(min = ApiConstants.AD_DESCRIPTION_MIN_LENGTH, max = ApiConstants.AD_DESCRIPTION_MAX_LENGTH)
    @Column(nullable = false, length = ApiConstants.AD_DESCRIPTION_MAX_LENGTH)
    private String description;

    @NotNull
    @Min(ApiConstants.AD_PRICE_MIN)
    @Max(ApiConstants.AD_PRICE_MAX)
    @Column(nullable = false)
    private Integer price;

    /** Публичный URL изображения объявления (например {@code /images/ads/{uuid}_photo.jpg}). */
    @Size(max = ApiConstants.IMAGE_PATH_MAX_LENGTH)
    @Column(length = ApiConstants.IMAGE_PATH_MAX_LENGTH)
    private String image;

    /** Пользователь, разместивший объявление. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}
