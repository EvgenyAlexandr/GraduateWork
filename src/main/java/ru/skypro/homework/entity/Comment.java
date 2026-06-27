package ru.skypro.homework.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.skypro.homework.constant.ApiConstants;

/**
 * Сущность комментария к объявлению.
 * <p>
 * Дата создания хранится в БД как {@link LocalDateTime} и конвертируется в timestamp при отдаче API.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {

    /** Первичный ключ комментария. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;

    /** Текст комментария. */
    @NotBlank
    @Size(min = ApiConstants.COMMENT_TEXT_MIN_LENGTH, max = ApiConstants.COMMENT_TEXT_MAX_LENGTH)
    @Column(nullable = false, length = ApiConstants.COMMENT_TEXT_MAX_LENGTH)
    private String text;

    /** Момент публикации комментария. */
    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** Автор комментария. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /** Объявление, к которому относится комментарий. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_pk", nullable = false)
    private Ad ad;
}
