package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.entity.User;

/**
 * MapStruct-маппер: {@link ru.skypro.homework.entity.Ad} ↔ DTO объявлений.
 */
@Mapper(componentModel = "spring")
public interface AdMapper {

    /**
     * Краткое DTO: идентификатор автора берётся из связанной сущности {@link User}.
     */
    @Mapping(target = "author", source = "author.id")
    ru.skypro.homework.dto.Ad toDto(ru.skypro.homework.entity.Ad ad);

    /**
     * Расширенное DTO с контактными данными автора для карточки объявления.
     */
    @Mapping(target = "authorFirstName", source = "author.firstName")
    @Mapping(target = "authorLastName", source = "author.lastName")
    @Mapping(target = "email", source = "author.email")
    @Mapping(target = "phone", source = "author.phone")
    ExtendedAd toExtendedDto(ru.skypro.homework.entity.Ad ad);

    /**
     * Создаёт сущность объявления; изображение задаётся отдельно на Этапе IV.
     */
    @Mapping(target = "pk", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "author", source = "author")
    ru.skypro.homework.entity.Ad toEntity(CreateOrUpdateAd createOrUpdateAd, User author);

    /**
     * Обновляет текстовые поля и цену; автор и изображение не изменяются.
     */
    @Mapping(target = "pk", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "author", ignore = true)
    void updateEntityFromDto(CreateOrUpdateAd createOrUpdateAd, @MappingTarget ru.skypro.homework.entity.Ad ad);
}
