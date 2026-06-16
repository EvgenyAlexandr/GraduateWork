package ru.skypro.homework.service;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.ResourceNotFoundException;

/**
 * Контракт сервиса объявлений: CRUD, маппинг Entity ↔ DTO и проверка прав доступа.
 */
public interface AdService {

    /**
     * Возвращает все объявления в формате {@link Ads}.
     */
    Ads getAllAds();

    /**
     * Возвращает объявления указанного автора.
     *
     * @param authorId идентификатор пользователя
     */
    Ads getAdsByAuthorId(Integer authorId);

    /**
     * Возвращает расширенное DTO объявления с данными автора.
     *
     * @param id первичный ключ объявления
     * @throws ResourceNotFoundException если объявление не найдено
     */
    ExtendedAd getExtendedAd(Integer id);

    /**
     * Проверяет существование объявления.
     *
     * @param id первичный ключ объявления
     * @throws ResourceNotFoundException если объявление не найдено
     */
    void requireAdExists(Integer id);

    /**
     * Возвращает сущность объявления по идентификатору.
     *
     * @param id первичный ключ объявления
     */
    Optional<Ad> findEntityById(Integer id);

    /**
     * Создаёт объявление, сохраняет изображение на диск и записывает путь в БД.
     *
     * @param createOrUpdateAd данные из запроса
     * @param author           автор объявления
     * @param image            файл изображения из multipart-запроса
     * @return сохранённая сущность
     */
    Ad createAd(CreateOrUpdateAd createOrUpdateAd, User author, MultipartFile image);

    /**
     * Обновляет поля объявления с проверкой прав владельца или ADMIN.
     *
     * @param id               первичный ключ объявления
     * @param createOrUpdateAd новые значения
     * @param currentUser      текущий пользователь
     * @return обновлённая сущность
     */
    Ad updateAd(Integer id, CreateOrUpdateAd createOrUpdateAd, User currentUser);

    /**
     * Удаляет объявление с проверкой прав владельца или ADMIN.
     *
     * @param id          первичный ключ объявления
     * @param currentUser текущий пользователь
     */
    void deleteAd(Integer id, User currentUser);

    /**
     * Заменяет изображение объявления и возвращает байты сохранённого файла.
     *
     * @param id          первичный ключ объявления
     * @param image       новый файл изображения
     * @param currentUser текущий пользователь
     * @return содержимое файла для ответа {@code application/octet-stream}
     */
    byte[] updateAdImage(Integer id, MultipartFile image, User currentUser);

    /**
     * Преобразует сущность в краткое DTO для ответа API.
     */
    ru.skypro.homework.dto.Ad toDto(Ad ad);
}
