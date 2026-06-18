package ru.skypro.homework.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
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
 * Пробелы и спецсимволы в имени файла заменяются при сохранении; URL кодируется для корректной работы в браузере.
 * При замене или удалении изображения старый файл удаляется с диска через {@link #deleteByPublicUrl(String)}.
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
        Optional<Path> filePath = resolvePathFromPublicUrl(publicUrl);
        if (filePath.isEmpty()) {
            return new byte[0];
        }
        try {
            if (!Files.exists(filePath.get())) {
                return new byte[0];
            }
            return Files.readAllBytes(filePath.get());
        } catch (IOException exception) {
            throw new ImageStorageException("Не удалось прочитать файл: " + publicUrl, exception);
        }
    }

    /**
     * Удаляет файл с диска по публичному URL из поля {@code image} в БД.
     * Безопасно игнорирует {@code null}, пустые строки и пути вне {@link #PUBLIC_URL_PREFIX}.
     *
     * @param publicUrl URL, ранее сохранённый в сущности объявления или пользователя
     */
    public void deleteByPublicUrl(String publicUrl) {
        resolvePathFromPublicUrl(publicUrl).ifPresent(path -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException exception) {
                throw new ImageStorageException("Не удалось удалить файл: " + publicUrl, exception);
            }
        });
    }

    /**
     * Преобразует публичный URL в путь на диске относительно {@link #basePath}.
     */
    private Optional<Path> resolvePathFromPublicUrl(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank() || !publicUrl.startsWith(PUBLIC_URL_PREFIX)) {
            return Optional.empty();
        }
        String relativePath = decodeRelativePath(publicUrl.substring(PUBLIC_URL_PREFIX.length()));
        return Optional.of(Paths.get(basePath).resolve(relativePath));
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
            return buildPublicUrl(subdir, filename);
        } catch (IOException exception) {
            throw new ImageStorageException("Не удалось сохранить изображение", exception);
        }
    }

    /**
     * Формирует URL без пробелов и с percent-encoding для безопасной подстановки в {@code <img src>}.
     */
    private String buildPublicUrl(String subdir, String filename) {
        return ru.skypro.homework.util.ImageUrlUtils.encodeForBrowser(
                PUBLIC_URL_PREFIX + subdir + "/" + filename);
    }

    /**
     * Декодирует относительный путь из URL (поддержка старых записей с пробелами и {@code %20}).
     */
    private String decodeRelativePath(String relativePath) {
        try {
            return URLDecoder.decode(relativePath, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException exception) {
            return relativePath;
        }
    }

    /**
     * Убирает из имени файла символы, недопустимые в URL и файловой системе (включая пробелы).
     */
    private String sanitizeFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "image";
        }
        String name = Paths.get(originalFilename).getFileName().toString();
        String sanitized = name.replaceAll("[\\\\/:*?\"<>|\\s]+", "_");
        sanitized = sanitized.replaceAll("^_+", "");
        if (sanitized.isBlank()) {
            return "image";
        }
        return sanitized.length() > ApiConstants.IMAGE_FILENAME_MAX_LENGTH
                ? sanitized.substring(0, ApiConstants.IMAGE_FILENAME_MAX_LENGTH)
                : sanitized;
    }
}
