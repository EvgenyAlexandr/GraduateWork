package ru.skypro.homework.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.support.IntegrationTestData;

/**
 * Интеграционные тесты REST-контроллера объявлений.
 * <p>
 * Проверяют правила Spring Security (публичный {@code GET /ads}, Basic Auth для остального)
 * и запрет редактирования чужих объявлений на уровне HTTP (403).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User owner;
    private User otherUser;
    private User admin;
    private Ad ownerAd;

    /**
     * Перед каждым тестом создаём трёх пользователей и объявление владельца в H2.
     */
    @BeforeEach
    void setUp() {
        owner = IntegrationTestData.persistUser(
                userRepository, passwordEncoder,
                IntegrationTestData.USER_EMAIL, Role.USER, "Ivan", "Ivanov");
        otherUser = IntegrationTestData.persistUser(
                userRepository, passwordEncoder,
                IntegrationTestData.OTHER_EMAIL, Role.USER, "Petr", "Petrov");
        admin = IntegrationTestData.persistUser(
                userRepository, passwordEncoder,
                IntegrationTestData.ADMIN_EMAIL, Role.ADMIN, "Admin", "Adminov");
        ownerAd = IntegrationTestData.persistAd(adRepository, owner);
    }

    @Test
    @DisplayName("GET /ads без авторизации возвращает 200 и список объявлений")
    void getAllAds_withoutAuth_returnsOk() throws Exception {
        // Публичный эндпоинт — критерий Этапа III
        mockMvc.perform(get("/ads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.results[0].pk").value(ownerAd.getPk()));
    }

    @Test
    @DisplayName("GET /ads/me без Basic Auth возвращает 401 Unauthorized")
    void getAdsMe_withoutAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/ads/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /ads/me с Basic Auth возвращает объявления текущего пользователя")
    void getAdsMe_withAuth_returnsOwnerAds() throws Exception {
        mockMvc.perform(get("/ads/me")
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.results[0].author").value(owner.getId()));
    }

    @Test
    @DisplayName("POST /ads без авторизации возвращает 401")
    void createAd_withoutAuth_returnsUnauthorized() throws Exception {
        MockMultipartFile properties = adPropertiesPart();
        MockMultipartFile image = imagePart();

        mockMvc.perform(multipart("/ads")
                        .file(properties)
                        .file(image))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE чужого объявления обычным пользователем возвращает 403 Forbidden")
    void deleteAd_byOtherUser_returnsForbidden() throws Exception {
        // otherUser пытается удалить объявление owner — сервис выбрасывает AccessDeniedException
        mockMvc.perform(delete("/ads/" + ownerAd.getPk())
                        .with(httpBasic(IntegrationTestData.OTHER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE своего объявления владельцем возвращает 204 No Content")
    void deleteAd_byOwner_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/ads/" + ownerAd.getPk())
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isNoContent());

        assertThat(adRepository.findById(ownerAd.getPk())).isEmpty();
    }

    @Test
    @DisplayName("ADMIN может удалить чужое объявление — 204 No Content")
    void deleteAd_byAdmin_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/ads/" + ownerAd.getPk())
                        .with(httpBasic(IntegrationTestData.ADMIN_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isNoContent());
    }

    /** JSON-часть multipart для поля {@code properties} при создании объявления. */
    private MockMultipartFile adPropertiesPart() {
        String json = "{\"title\":\"New ad title\",\"description\":\"New ad description\",\"price\":500}";
        return new MockMultipartFile(
                "properties", "", MediaType.APPLICATION_JSON_VALUE, json.getBytes(StandardCharsets.UTF_8));
    }

    /** Файловая часть multipart — тестовое изображение объявления. */
    private MockMultipartFile imagePart() {
        return new MockMultipartFile("image", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[]{1, 2, 3});
    }
}
