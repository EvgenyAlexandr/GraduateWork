package ru.skypro.homework.exception;

/**
 * Исключение: запрашиваемый ресурс (объявление, комментарий) не найден в БД.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * @param message описание ошибки
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
