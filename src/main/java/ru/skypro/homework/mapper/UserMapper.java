package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.UpdateUser;

/**
 * MapStruct-маппер: {@link ru.skypro.homework.entity.User} ↔ {@link ru.skypro.homework.dto.User}.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Преобразует сущность пользователя в DTO для ответа API.
     */
    ru.skypro.homework.dto.User toDto(ru.skypro.homework.entity.User user);

    /**
     * Создаёт сущность из данных регистрации.
     * Поле {@code username} из DTO маппится в {@code email} сущности.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "email", source = "username")
    ru.skypro.homework.entity.User toEntity(Register register);

    /**
     * Обновляет редактируемые поля профиля; пароль, роль и email не изменяются.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "image", ignore = true)
    void updateEntityFromDto(UpdateUser updateUser, @MappingTarget ru.skypro.homework.entity.User user);
}
