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

import ru.skypro.homework.config.StorageProperties;

/**
 * Unit-тесты {@link ImageStorageService}: сохранение и чтение файлов на диске.
 */
class ImageStorageServiceTest {

    @TempDir
    Path tempDir;

    private ImageStorageService imageStorageService;

    @BeforeEach
    void setUp() {
        StorageProperties storageProperties = new StorageProperties();
        ReflectionTestUtils.setField(storageProperties, "absoluteBasePath", tempDir);
        imageStorageService = new ImageStorageService(storageProperties);
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

    @Test
    @DisplayName("saveAdImage заменяет пробелы в имени файла и возвращает URL без пробелов")
    void saveAdImage_replacesSpacesInFilename() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image", "my photo.jpg", "image/jpeg", "jpeg-content".getBytes());

        String url = imageStorageService.saveAdImage(file);

        assertThat(url).doesNotContain(" ");
        assertThat(url).contains("my_photo.jpg");
        assertThat(imageStorageService.readByPublicUrl(url)).isEqualTo("jpeg-content".getBytes());
    }

    @Test
    @DisplayName("deleteByPublicUrl удаляет сохранённый файл с диска")
    void deleteByPublicUrl_removesFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image", "photo.jpg", "image/jpeg", "jpeg-content".getBytes());

        String url = imageStorageService.saveAdImage(file);
        assertThat(imageStorageService.readByPublicUrl(url)).isNotEmpty();

        imageStorageService.deleteByPublicUrl(url);

        assertThat(imageStorageService.readByPublicUrl(url)).isEmpty();
    }

    @Test
    @DisplayName("deleteByPublicUrl безопасно обрабатывает null и неизвестный URL")
    void deleteByPublicUrl_ignoresInvalidUrls() {
        imageStorageService.deleteByPublicUrl(null);
        imageStorageService.deleteByPublicUrl("");
        imageStorageService.deleteByPublicUrl("/ads/image/1");
    }

    @Test
    @DisplayName("saveAvatarImage сохраняет кириллическое имя как ASCII и файл читается по URL")
    void saveAvatarImage_sanitizesCyrillicFilename() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image", "Сттийк.jpg", "image/jpeg", "avatar-bytes".getBytes());

        String url = imageStorageService.saveAvatarImage(file);

        assertThat(url).contains("image.jpg");
        assertThat(url).doesNotContain("Ст");
        assertThat(imageStorageService.readByPublicUrl(url)).isEqualTo("avatar-bytes".getBytes());
    }

    @Test
    void readByPublicUrl_decodesEncodedUrl() throws Exception {
        Path adsDir = tempDir.resolve("ads");
        Files.createDirectories(adsDir);
        Path file = adsDir.resolve("legacy file.jpg");
        Files.write(file, "legacy".getBytes());

        byte[] content = imageStorageService.readByPublicUrl("/images/ads/legacy%20file.jpg");

        assertThat(content).isEqualTo("legacy".getBytes());
    }
}
