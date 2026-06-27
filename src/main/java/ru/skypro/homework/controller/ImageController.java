package ru.skypro.homework.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.skypro.homework.service.ImageStorageService;

/**
 * Раздача загруженных изображений по URL {@code /images/ads/...} и {@code /images/avatars/...}.
 */
@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageStorageService imageStorageService;

    /**
     * Возвращает байты фото объявления.
     */
    @GetMapping("/images/ads/{filename:.+}")
    public ResponseEntity<byte[]> getAdImage(@PathVariable String filename) {
        return getImage(ImageStorageService.ADS_SUBDIR, filename);
    }

    /**
     * Возвращает байты аватара пользователя.
     */
    @GetMapping("/images/avatars/{filename:.+}")
    public ResponseEntity<byte[]> getAvatarImage(@PathVariable String filename) {
        return getImage(ImageStorageService.AVATARS_SUBDIR, filename);
    }

    private ResponseEntity<byte[]> getImage(String subdir, String filename) {
        String publicUrl = ImageStorageService.PUBLIC_URL_PREFIX + subdir + "/" + filename;
        byte[] content = imageStorageService.readByPublicUrl(publicUrl);
        if (content.length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }
}
