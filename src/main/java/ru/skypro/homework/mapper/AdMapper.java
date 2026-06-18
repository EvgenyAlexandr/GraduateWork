package ru.skypro.homework.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import ru.skypro.homework.dto.Ads;
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
    @Mapping(target = "image", expression = "java(ru.skypro.homework.util.ImageUrlUtils.encodeForBrowser(ad.getImage()))")
    ru.skypro.homework.dto.Ad toDto(ru.skypro.homework.entity.Ad ad);

    /**
     * Расширенное DTO с контактными данными автора для карточки объявления.
     */
    @Mapping(target = "authorFirstName", source = "author.firstName")
    @Mapping(target = "authorLastName", source = "author.lastName")
    @Mapping(target = "email", source = "author.email")
    @Mapping(target = "phone", source = "author.phone")
    @Mapping(target = "image", expression = "java(ru.skypro.homework.util.ImageUrlUtils.encodeForBrowser(ad.getImage()))")
    ExtendedAd toExtendedDto(ru.skypro.homework.entity.Ad ad);

    /**
     * Формирует обёртку {@link Ads} со счётчиком и списком кратких DTO.
     */
    default Ads toAdsDto(List<ru.skypro.homework.entity.Ad> ads) {
        Ads result = new Ads();
        if (ads == null || ads.isEmpty()) {
            result.setCount(0);
            return result;
        }
        List<ru.skypro.homework.dto.Ad> dtos = ads.stream().map(this::toDto).collect(Collectors.toList());
        result.setCount(dtos.size());
        result.setResults(dtos);
        return result;
    }

    /**
     * Создаёт сущность объявления; путь к изображению задаётся в сервисе после сохранения файла.
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
