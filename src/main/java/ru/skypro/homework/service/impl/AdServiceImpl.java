package ru.skypro.homework.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.ResourceNotFoundException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.security.AccessChecker;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.ImageStorageService;

/**
 * Реализация {@link AdService}: CRUD объявлений через {@link AdRepository}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final AccessChecker accessChecker;
    private final ImageStorageService imageStorageService;

    /** {@inheritDoc} */
    @Override
    public Ads getAllAds() {
        return adMapper.toAdsDto(adRepository.findAll());
    }

    /** {@inheritDoc} */
    @Override
    public Ads getAdsByAuthorId(Integer authorId) {
        return adMapper.toAdsDto(adRepository.findAllByAuthor_Id(authorId));
    }

    /** {@inheritDoc} */
    @Override
    public ExtendedAd getExtendedAd(Integer id) {
        return adRepository.findById(id)
                .map(adMapper::toExtendedDto)
                .orElseThrow(() -> new ResourceNotFoundException("Объявление не найдено: " + id));
    }

    /** {@inheritDoc} */
    @Override
    public void requireAdExists(Integer id) {
        if (!adRepository.existsById(id)) {
            throw new ResourceNotFoundException("Объявление не найдено: " + id);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Ad> findEntityById(Integer id) {
        return adRepository.findById(id);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Ad createAd(CreateOrUpdateAd createOrUpdateAd, User author, MultipartFile image) {
        Ad ad = adMapper.toEntity(createOrUpdateAd, author);
        ad.setImage(imageStorageService.saveAdImage(image));
        return adRepository.save(ad);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Ad updateAd(Integer id, CreateOrUpdateAd createOrUpdateAd, User currentUser) {
        Ad ad = findAdOrThrow(id);
        accessChecker.checkOwnerOrAdmin(ad.getAuthor(), currentUser);
        adMapper.updateEntityFromDto(createOrUpdateAd, ad);
        return adRepository.save(ad);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void deleteAd(Integer id, User currentUser) {
        Ad ad = findAdOrThrow(id);
        accessChecker.checkOwnerOrAdmin(ad.getAuthor(), currentUser);
        imageStorageService.deleteByPublicUrl(ad.getImage());
        adRepository.delete(ad);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public byte[] updateAdImage(Integer id, MultipartFile image, User currentUser) {
        Ad ad = findAdOrThrow(id);
        accessChecker.checkOwnerOrAdmin(ad.getAuthor(), currentUser);
        imageStorageService.deleteByPublicUrl(ad.getImage());
        String imageUrl = imageStorageService.saveAdImage(image);
        ad.setImage(imageUrl);
        adRepository.save(ad);
        return imageStorageService.readByPublicUrl(imageUrl);
    }

    /** {@inheritDoc} */
    @Override
    public ru.skypro.homework.dto.Ad toDto(Ad ad) {
        return adMapper.toDto(ad);
    }

    private Ad findAdOrThrow(Integer id) {
        return adRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Объявление не найдено: " + id));
    }
}
