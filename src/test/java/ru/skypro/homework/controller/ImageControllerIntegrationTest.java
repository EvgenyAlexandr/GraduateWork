package ru.skypro.homework.controller;

import static org.hamcrest.Matchers.startsWith;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import ru.skypro.homework.service.ImageStorageService;
import ru.skypro.homework.support.IntegrationTestData;

/**
 * Интеграционные тесты Этапа IV: загрузка, обновление и публичная раздача изображений.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ImageControllerIntegrationTest {

    private static final byte[] JPEG_BYTES = new byte[]{(byte) 0xFF, (byte) 0xD8, 0x01, 0x02};

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ImageStorageService imageStorageService;

    private User owner;
    private Ad ownerAd;

    @BeforeEach
    void setUp() {
        owner = IntegrationTestData.persistUser(
                userRepository, passwordEncoder,
                IntegrationTestData.USER_EMAIL, Role.USER, "Ivan", "Ivanov");
        ownerAd = IntegrationTestData.persistAd(adRepository, owner);
    }

    @Test
    @DisplayName("GET /images/ads/{file} без авторизации отдаёт байты файла")
    void getAdImage_withoutAuth_returnsFileBytes() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, JPEG_BYTES);
        String imageUrl = imageStorageService.saveAdImage(image);
        String relativePath = imageUrl.substring("/images/".length());

        mockMvc.perform(get("/images/" + relativePath))
                .andExpect(status().isOk())
                .andExpect(content().bytes(JPEG_BYTES));
    }

    @Test
    @DisplayName("GET /images/avatars/{file} отдаёт аватар с кириллическим исходным именем файла")
    void getAvatarImage_withCyrillicOriginalFilename_returnsFileBytes() throws Exception {
        MockMultipartFile avatar = new MockMultipartFile(
                "image", "Сттийк.jpg", MediaType.IMAGE_JPEG_VALUE, JPEG_BYTES);
        String imageUrl = imageStorageService.saveAvatarImage(avatar);
        String relativePath = imageUrl.substring("/images/".length());

        mockMvc.perform(get("/images/" + relativePath))
                .andExpect(status().isOk())
                .andExpect(content().bytes(JPEG_BYTES));
    }

    @Test
    @DisplayName("PATCH /users/me/image сохраняет аватар и GET /users/me возвращает URL")
    void updateUserImage_savesAvatarAndReturnsUrlInProfile() throws Exception {
        MockMultipartFile avatar = new MockMultipartFile(
                "image", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE, JPEG_BYTES);

        mockMvc.perform(multipart("/users/me/image")
                        .file(avatar)
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD))
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.image").value(startsWith("/images/avatars/")));

        User updated = userRepository.findByEmail(IntegrationTestData.USER_EMAIL).orElseThrow();
        assertThat(updated.getImage()).startsWith("/images/avatars/");

        mockMvc.perform(get("/users/me")
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.image").value(updated.getImage()));

        mockMvc.perform(get(updated.getImage()))
                .andExpect(status().isOk())
                .andExpect(content().bytes(JPEG_BYTES));
    }

    @Test
    @DisplayName("PATCH /ads/{id}/image обновляет картинку и возвращает байты")
    void updateAdImage_byOwner_returnsImageBytes() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image", "new-photo.jpg", MediaType.IMAGE_JPEG_VALUE, JPEG_BYTES);

        mockMvc.perform(multipart("/ads/" + ownerAd.getPk() + "/image")
                        .file(image)
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD))
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(content().bytes(JPEG_BYTES));

        Ad updated = adRepository.findById(ownerAd.getPk()).orElseThrow();
        assertThat(updated.getImage()).startsWith("/images/ads/");
    }

    @Test
    @DisplayName("POST /ads с изображением сохраняет URL картинки в ответе")
    void createAd_withImage_returnsImageUrl() throws Exception {
        MockMultipartFile properties = new MockMultipartFile(
                "properties", "",
                MediaType.APPLICATION_JSON_VALUE,
                "{\"title\":\"Photo ad\",\"description\":\"With image attached\",\"price\":1500}"
                        .getBytes(StandardCharsets.UTF_8));
        MockMultipartFile image = new MockMultipartFile(
                "image", "listing.jpg", MediaType.IMAGE_JPEG_VALUE, JPEG_BYTES);

        mockMvc.perform(multipart("/ads")
                        .file(properties)
                        .file(image)
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.image").value(startsWith("/images/ads/")));
    }
}
