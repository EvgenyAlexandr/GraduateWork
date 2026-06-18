package ru.skypro.homework.exception;

/**
 * Ошибка при сохранении, чтении или удалении файла изображения на диске.
 */
public class ImageStorageException extends RuntimeException {

    /**
     * @param message описание ошибки для логов
     * @param cause   исходное IO-исключение
     */
    public ImageStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
