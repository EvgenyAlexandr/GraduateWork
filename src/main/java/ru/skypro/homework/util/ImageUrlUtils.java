package ru.skypro.homework.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import ru.skypro.homework.service.ImageStorageService;

/**
 * Кодирование URL изображений для безопасной подстановки в {@code <img src>} на фронтенде.
 */
public final class ImageUrlUtils {

    private ImageUrlUtils() {
    }

    /**
     * Кодирует имя файла в URL (пробелы → {@code %20}), не изменяя префикс {@code /images/.../}.
     *
     * @param publicUrl путь из БД или только что сохранённый URL
     * @return URL, пригодный для HTTP-запроса из браузера
     */
    public static String encodeForBrowser(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) {
            return publicUrl;
        }
        if (!publicUrl.startsWith(ImageStorageService.PUBLIC_URL_PREFIX)) {
            return publicUrl;
        }
        int lastSlash = publicUrl.lastIndexOf('/');
        if (lastSlash < 0 || lastSlash >= publicUrl.length() - 1) {
            return publicUrl;
        }
        String pathPrefix = publicUrl.substring(0, lastSlash + 1);
        String filename = publicUrl.substring(lastSlash + 1);
        try {
            String decodedFilename = URLDecoder.decode(filename, StandardCharsets.UTF_8.name());
            String encodedFilename = URLEncoder.encode(decodedFilename, StandardCharsets.UTF_8.name())
                    .replace("+", "%20");
            return pathPrefix + encodedFilename;
        } catch (UnsupportedEncodingException exception) {
            return publicUrl;
        }
    }
}
