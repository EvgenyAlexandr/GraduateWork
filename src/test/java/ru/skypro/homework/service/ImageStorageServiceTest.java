package ru.skypro.homework.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit-тесты {@link ImageStorageService}: сохранение и чтение файлов на диске.
 */
class ImageStorageServiceTest {

    @TempDir
    Path tempDir;

    private ImageStorageService imageStorageService;

    @BeforeEach
    void setUp() {
        imageStorageService = new ImageStorageService();
        ReflectionTestUtils.setField(imageStorageService, "basePath", tempDir.toString());
    }

    @Test
    @DisplayName("saveAdImage сохраняет файл и возвращает публичный URL")
    void saveAdImage_writesFileAndReturnsUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image", "photo.jpg", "image/jpeg", "jpeg-content".getBytes());

        String url = imageStorageService.saveAdImage(file);

        assertThat(url).startsWith("/images/ads/");
        assertThat(Files.exists(tempDir.resolve("ads"))).isTrue();
        assertThat(imageStorageService.readByPublicUrl(url)).isEqualTo("jpeg-content".getBytes());
    }

    @Test
    @DisplayName("saveAvatarImage сохраняет файл в подкаталог avatars")
    void saveAvatarImage_writesToAvatarsSubdir() {
        MockMultipartFile file = new MockMultipartFile(
                "image", "avatar.png", "image/png", new byte[]{1, 2, 3});

        String url = imageStorageService.saveAvatarImage(file);

        assertThat(url).startsWith("/images/avatars/");
    }
}
