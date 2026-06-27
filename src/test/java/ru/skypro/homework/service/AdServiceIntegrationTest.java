package ru.skypro.homework.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.ResourceNotFoundException;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.support.IntegrationTestData;

/**
 * Интеграционные тесты {@link AdService} / {@link ru.skypro.homework.service.impl.AdServiceImpl}.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdServiceIntegrationTest {

    @Autowired
    private AdService adService;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User owner;
    private User stranger;
    private User admin;
    private Ad ad;

    @BeforeEach
    void setUp() {
        owner = IntegrationTestData.persistUser(
                userRepository, passwordEncoder,
                IntegrationTestData.USER_EMAIL, Role.USER, "Ivan", "Ivanov");
        stranger = IntegrationTestData.persistUser(
                userRepository, passwordEncoder,
                IntegrationTestData.OTHER_EMAIL, Role.USER, "Petr", "Petrov");
        admin = IntegrationTestData.persistUser(
                userRepository, passwordEncoder,
                IntegrationTestData.ADMIN_EMAIL, Role.ADMIN, "Admin", "Adminov");
        ad = IntegrationTestData.persistAd(adRepository, owner);
    }

    @Test
    @DisplayName("getAllAds возвращает объявления, сохранённые в БД")
    void getAllAds_returnsPersistedAds() {
        assertThat(adService.getAllAds().getCount()).isEqualTo(1);
        assertThat(adService.getAllAds().getResults().get(0).getPk()).isEqualTo(ad.getPk());
    }

    @Test
    @DisplayName("updateAd чужим пользователем выбрасывает AccessDeniedException")
    void updateAd_byStranger_throwsAccessDenied() {
        CreateOrUpdateAd dto = new CreateOrUpdateAd();
        dto.setTitle("Hacked title");
        dto.setDescription("Hacked description");
        dto.setPrice(999);

        assertThatThrownBy(() -> adService.updateAd(ad.getPk(), dto, stranger))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("updateAd администратором успешно изменяет чужое объявление")
    void updateAd_byAdmin_updatesAd() {
        CreateOrUpdateAd dto = new CreateOrUpdateAd();
        dto.setTitle("Admin edit");
        dto.setDescription("Changed by admin");
        dto.setPrice(2_000);

        Ad updated = adService.updateAd(ad.getPk(), dto, admin);

        assertThat(updated.getTitle()).isEqualTo("Admin edit");
        assertThat(updated.getPrice()).isEqualTo(2_000);
    }

    @Test
    @DisplayName("deleteAd несуществующего объявления выбрасывает ResourceNotFoundException")
    void deleteAd_notFound_throwsResourceNotFound() {
        assertThatThrownBy(() -> adService.deleteAd(9_999, owner))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("createAd сохраняет объявление с путём к изображению на диске")
    void createAd_persistsAdWithImagePath() {
        CreateOrUpdateAd dto = new CreateOrUpdateAd();
        dto.setTitle("Brand new");
        dto.setDescription("Brand new ad text");
        dto.setPrice(100);

        org.springframework.mock.web.MockMultipartFile image = new org.springframework.mock.web.MockMultipartFile(
                "image", "photo.jpg", "image/jpeg", "test-image".getBytes());

        Ad created = adService.createAd(dto, owner, image);

        assertThat(created.getPk()).isNotNull();
        assertThat(created.getImage()).startsWith("/images/ads/");
    }
}
