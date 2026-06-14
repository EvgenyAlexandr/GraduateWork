package ru.skypro.homework.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.skypro.homework.constant.ApiConstants;
import ru.skypro.homework.dto.Role;

/**
 * Сущность пользователя платформы.
 * <p>
 * Поле {@code email} хранит логин пользователя согласно OpenAPI-спецификации.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    /** Первичный ключ пользователя. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(min = ApiConstants.USERNAME_MIN_LENGTH, max = ApiConstants.USERNAME_MAX_LENGTH)
    @Column(nullable = false, unique = true, length = ApiConstants.USERNAME_MAX_LENGTH)
    private String email;

    @NotBlank
    @Size(max = ApiConstants.PASSWORD_HASH_MAX_LENGTH)
    @Column(nullable = false, length = ApiConstants.PASSWORD_HASH_MAX_LENGTH)
    private String password;

    @NotBlank
    @Size(min = ApiConstants.FIRST_NAME_MIN_LENGTH, max = ApiConstants.FIRST_NAME_MAX_LENGTH)
    @Column(name = "first_name", nullable = false, length = ApiConstants.FIRST_NAME_MAX_LENGTH)
    private String firstName;

    @NotBlank
    @Size(min = ApiConstants.LAST_NAME_MIN_LENGTH, max = ApiConstants.LAST_NAME_MAX_LENGTH)
    @Column(name = "last_name", nullable = false, length = ApiConstants.LAST_NAME_MAX_LENGTH)
    private String lastName;

    @Size(max = ApiConstants.PHONE_MAX_LENGTH)
    @Column(length = ApiConstants.PHONE_MAX_LENGTH)
    private String phone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    /** Публичный URL аватара пользователя (например {@code /images/avatars/{uuid}_avatar.jpg}). */
    @Size(max = ApiConstants.IMAGE_PATH_MAX_LENGTH)
    @Column(length = ApiConstants.IMAGE_PATH_MAX_LENGTH)
    private String image;
}
