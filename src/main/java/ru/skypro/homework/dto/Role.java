package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Роль пользователя в системе.
 * <p>
 * {@link #USER} — обычный пользователь, {@link #ADMIN} — администратор с расширенными правами.
 */
@Schema(description = "Роль пользователя")
public enum Role {

    /** Обычный пользователь: управляет только своими объявлениями и комментариями. */
    @Schema(description = "Пользователь")
    USER,

    /** Администратор: может редактировать и удалять любые объявления и комментарии. */
    @Schema(description = "Администратор")
    ADMIN
}
