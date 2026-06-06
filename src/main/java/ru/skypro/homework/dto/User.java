package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO профиля пользователя для ответов API.
 * <p>
 * Соответствует схеме {@code User} из {@code openapi.yaml}.
 * Поле {@code email} хранит логин пользователя согласно спецификации.
 */
@Data
@Schema(name = "User", description = "Пользователь")
public class User {

    @Schema(description = "id пользователя")
    private Integer id;

    @Schema(description = "логин пользователя")
    private String email;

    @Schema(description = "имя пользователя")
    private String firstName;

    @Schema(description = "фамилия пользователя")
    private String lastName;

    @Schema(description = "телефон пользователя")
    private String phone;

    @Schema(description = "роль пользователя", implementation = Role.class)
    private Role role;

    @Schema(description = "ссылка на аватар пользователя")
    private String image;
}
