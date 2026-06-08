package ru.skypro.homework.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdRepository;

/**
 * Сервис объявлений: маппинг Entity ↔ DTO и работа с репозиторием.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdService {

    private final AdRepository adRepository;
    private final AdMapper adMapper;

    /**
     * Возвращает все объявления в формате {@link Ads} (список и общее количество).
     */
    public Ads getAllAds() {
        List<ru.skypro.homework.dto.Ad> results = adRepository.findAll().stream()
                .map(adMapper::toDto)
                .collect(Collectors.toList());
        Ads ads = new Ads();
        ads.setCount(results.size());
        ads.setResults(results);
        return ads;
    }

    /**
     * Возвращает объявления конкретного автора.
     *
     * @param authorId идентификатор пользователя-автора
     */
    public Ads getAdsByAuthorId(Integer authorId) {
        List<ru.skypro.homework.dto.Ad> results = adRepository.findAllByAuthor_Id(authorId).stream()
                .map(adMapper::toDto)
                .collect(Collectors.toList());
        Ads ads = new Ads();
        ads.setCount(results.size());
        ads.setResults(results);
        return ads;
    }

    /**
     * Возвращает расширенное DTO объявления с данными автора.
     *
     * @param id первичный ключ объявления
     */
    public Optional<ExtendedAd> getExtendedAd(Integer id) {
        return adRepository.findById(id).map(adMapper::toExtendedDto);
    }

    /**
     * Возвращает краткое DTO объявления по идентификатору.
     *
     * @param id первичный ключ объявления
     */
    public Optional<ru.skypro.homework.dto.Ad> getAdDto(Integer id) {
        return adRepository.findById(id).map(adMapper::toDto);
    }

    /**
     * Создаёт объявление и сохраняет его в БД.
     *
     * @param createOrUpdateAd данные из запроса
     * @param author           пользователь-автор объявления
     * @return сохранённая сущность
     */
    @Transactional
    public Ad createAd(CreateOrUpdateAd createOrUpdateAd, User author) {
        Ad ad = adMapper.toEntity(createOrUpdateAd, author);
        return adRepository.save(ad);
    }

    /**
     * Обновляет поля объявления по идентификатору.
     *
     * @param id               первичный ключ объявления
     * @param createOrUpdateAd новые значения title, description, price
     * @return обновлённая сущность или пустой {@link Optional}, если объявление не найдено
     */
    @Transactional
    public Optional<Ad> updateAd(Integer id, CreateOrUpdateAd createOrUpdateAd) {
        return adRepository.findById(id).map(ad -> {
            adMapper.updateEntityFromDto(createOrUpdateAd, ad);
            return adRepository.save(ad);
        });
    }

    /**
     * Удаляет объявление по идентификатору.
     *
     * @param id первичный ключ объявления
     * @return {@code true}, если объявление было удалено; {@code false}, если не найдено
     */
    @Transactional
    public boolean deleteAd(Integer id) {
        if (!adRepository.existsById(id)) {
            return false;
        }
        adRepository.deleteById(id);
        return true;
    }

    /**
     * Преобразует сущность в краткое DTO для ответа API.
     */
    public ru.skypro.homework.dto.Ad toDto(Ad ad) {
        return adMapper.toDto(ad);
    }
}
