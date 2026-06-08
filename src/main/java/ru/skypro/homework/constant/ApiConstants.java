package ru.skypro.homework.constant;

/**
 * Общие константы API: ограничения валидации, CORS и вспомогательные значения.
 * <p>
 * Синхронизированы с ограничениями из {@code openapi.yaml}.
 */
public final class ApiConstants {

    /** Адрес фронтенда для настройки CORS. */
    public static final String CORS_ORIGIN = "http://localhost:3000";

    /** Регулярное выражение для проверки российского номера телефона. */
    public static final String PHONE_PATTERN = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}";

    /** Минимальная длина пароля. */
    public static final int PASSWORD_MIN_LENGTH = 8;

    /** Максимальная длина пароля. */
    public static final int PASSWORD_MAX_LENGTH = 16;

    /** Минимальная длина логина. */
    public static final int USERNAME_MIN_LENGTH = 4;

    /** Максимальная длина логина. */
    public static final int USERNAME_MAX_LENGTH = 32;

    /** Минимальная длина имени при регистрации. */
    public static final int FIRST_NAME_MIN_LENGTH = 2;

    /** Максимальная длина имени при регистрации. */
    public static final int FIRST_NAME_MAX_LENGTH = 16;

    /** Минимальная длина фамилии при регистрации. */
    public static final int LAST_NAME_MIN_LENGTH = 2;

    /** Максимальная длина фамилии при регистрации. */
    public static final int LAST_NAME_MAX_LENGTH = 16;

    /** Минимальная длина имени при обновлении профиля. */
    public static final int UPDATE_FIRST_NAME_MIN_LENGTH = 3;

    /** Максимальная длина имени при обновлении профиля. */
    public static final int UPDATE_FIRST_NAME_MAX_LENGTH = 10;

    /** Минимальная длина фамилии при обновлении профиля. */
    public static final int UPDATE_LAST_NAME_MIN_LENGTH = 3;

    /** Максимальная длина фамилии при обновлении профиля. */
    public static final int UPDATE_LAST_NAME_MAX_LENGTH = 10;

    /** Минимальная длина заголовка объявления. */
    public static final int AD_TITLE_MIN_LENGTH = 4;

    /** Максимальная длина заголовка объявления. */
    public static final int AD_TITLE_MAX_LENGTH = 32;

    /** Минимальная длина описания объявления. */
    public static final int AD_DESCRIPTION_MIN_LENGTH = 8;

    /** Максимальная длина описания объявления. */
    public static final int AD_DESCRIPTION_MAX_LENGTH = 64;

    /** Минимально допустимая цена объявления. */
    public static final int AD_PRICE_MIN = 0;

    /** Максимально допустимая цена объявления. */
    public static final int AD_PRICE_MAX = 10_000_000;

    /** Минимальная длина текста комментария. */
    public static final int COMMENT_TEXT_MIN_LENGTH = 8;

    /** Максимальная длина текста комментария. */
    public static final int COMMENT_TEXT_MAX_LENGTH = 64;

    /** Пустой массив байт для заглушки ответа при обновлении изображения (Этап I). */
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /** Максимальная длина хэша пароля в БД (BCrypt). */
    public static final int PASSWORD_HASH_MAX_LENGTH = 255;

    /** Максимальная длина пути к файлу изображения в БД. */
    public static final int IMAGE_PATH_MAX_LENGTH = 255;

    /** Максимальная длина телефона в БД. */
    public static final int PHONE_MAX_LENGTH = 20;

    private ApiConstants() {
    }
}
