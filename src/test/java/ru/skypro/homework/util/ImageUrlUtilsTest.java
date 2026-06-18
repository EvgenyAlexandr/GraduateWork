package ru.skypro.homework.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit-тесты {@link ImageUrlUtils}.
 */
class ImageUrlUtilsTest {

    @Test
    @DisplayName("encodeForBrowser кодирует пробелы в URL изображения")
    void encodeForBrowser_encodesSpaces() {
        String encoded = ImageUrlUtils.encodeForBrowser("/images/ads/uuid_my photo.jpg");

        assertThat(encoded).isEqualTo("/images/ads/uuid_my%20photo.jpg");
    }

    @Test
    @DisplayName("encodeForBrowser не изменяет уже корректный URL")
    void encodeForBrowser_leavesEncodedUrlUnchanged() {
        String url = "/images/ads/uuid_my%20photo.jpg";

        assertThat(ImageUrlUtils.encodeForBrowser(url)).isEqualTo(url);
    }
}
