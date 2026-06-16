package ru.skypro.homework.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import ru.skypro.homework.dto.CreateOrUpdateComment;

/**
 * MapStruct-маппер: {@link ru.skypro.homework.entity.Comment} ↔ {@link ru.skypro.homework.dto.Comment}.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

    /**
     * DTO для ответа API: дата создания конвертируется в Unix timestamp (миллисекунды).
     */
    @Mapping(target = "author", source = "author.id")
    @Mapping(target = "authorFirstName", source = "author.firstName")
    @Mapping(target = "authorImage", source = "author.image")
    @Mapping(target = "createdAt", expression = "java(toEpochMilli(comment.getCreatedAt()))")
    ru.skypro.homework.dto.Comment toDto(ru.skypro.homework.entity.Comment comment);

    /**
     * Создаёт сущность из текста; автор, объявление и дата проставляются в сервисе.
     */
    @Mapping(target = "pk", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "ad", ignore = true)
    ru.skypro.homework.entity.Comment toEntity(CreateOrUpdateComment createOrUpdateComment);

    /**
     * Обновляет только текст комментария.
     */
    @Mapping(target = "pk", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "ad", ignore = true)
    void updateEntityFromDto(CreateOrUpdateComment createOrUpdateComment,
            @MappingTarget ru.skypro.homework.entity.Comment comment);

    /**
     * Конвертирует {@link LocalDateTime} в миллисекунды с начала эпохи (системная зона).
     */
    default Long toEpochMilli(LocalDateTime createdAt) {
        if (createdAt == null) {
            return null;
        }
        return createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
