package ru.skypro.homework.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.skypro.homework.constant.ApiConstants;
import ru.skypro.homework.exception.ImageStorageException;

/**
 * Сохранение и чтение файлов изображений объявлений и аватаров на диске.
 * <p>
 * Публичный URL вида {@code /images/ads/{uuid}_{имя}} отдаётся через {@link ru.skypro.homework.config.WebConfig}.
 */
@Service
public class ImageStorageService {

    /** Подкаталог для фото объявлений относительно {@link #basePath}. */
    public static final String ADS_SUBDIR = "ads";

    /** Подкаталог для аватаров пользователей относительно {@link #basePath}. */
    public static final String AVATARS_SUBDIR = "avatars";

    /** Префикс URL, по которому Spring отдаёт статические файлы. */
    public static final String PUBLIC_URL_PREFIX = "/images/";

    @Value("${app.storage.base-path:images}")
    private String basePath;

    /**
     * Сохраняет изображение объявления и возвращает публичный URL для поля {@code image} в DTO.
     *
     * @param image загруженный файл из multipart-запроса
     * @return URL вида {@code /images/ads/{uuid}_{имя_файла}}
     */
    public String saveAdImage(MultipartFile image) {
        return saveImage(image, ADS_SUBDIR);
    }

    /**
     * Сохраняет аватар пользователя и возвращает публичный URL для поля {@code image} в DTO.
     *
     * @param image загруженный файл из multipart-запроса
     * @return URL вида {@code /images/avatars/{uuid}_{имя_файла}}
     */
    public String saveAvatarImage(MultipartFile image) {
        return saveImage(image, AVATARS_SUBDIR);
    }

    /**
     * Читает байты файла по публичному URL, сохранённому в БД.
     *
     * @param publicUrl путь из поля {@code image} сущности
     * @return содержимое файла для ответа {@code application/octet-stream}
     */
    public byte[] readByPublicUrl(String publicUrl) {
        if (publicUrl == null || !publicUrl.startsWith(PUBLIC_URL_PREFIX)) {
            return new byte[0];
        }
        String relativePath = publicUrl.substring(PUBLIC_URL_PREFIX.length());
        Path filePath = Paths.get(basePath).resolve(relativePath);
        try {
            if (!Files.exists(filePath)) {
                return new byte[0];
            }
            return Files.readAllBytes(filePath);
        } catch (IOException exception) {
            throw new ImageStorageException("Не удалось прочитать файл: " + publicUrl, exception);
        }
    }

    private String saveImage(MultipartFile image, String subdir) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Файл изображения не передан");
        }
        String filename = UUID.randomUUID() + "_" + sanitizeFilename(image.getOriginalFilename());
        Path directory = Paths.get(basePath, subdir);
        try {
            Files.createDirectories(directory);
            Path target = directory.resolve(filename);
            // transferTo закрывает временный файл Tomcat до cleanup multipart (важно для Windows)
            image.transferTo(target);
            return PUBLIC_URL_PREFIX + subdir + "/" + filename;
        } catch (IOException exception) {
            throw new ImageStorageException("Не удалось сохранить изображение", exception);
        }
    }

    /**
     * Убирает из имени файла символы, недопустимые в пути файловой системы.
     */
    private String sanitizeFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "image";
        }
        String sanitized = originalFilename.replaceAll("[\\\\/:*?\"<>|]", "_");
        return sanitized.length() > ApiConstants.IMAGE_FILENAME_MAX_LENGTH
                ? sanitized.substring(0, ApiConstants.IMAGE_FILENAME_MAX_LENGTH)
                : sanitized;
    }
}
