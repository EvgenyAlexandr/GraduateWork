package ru.skypro.homework.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * Абсолютный путь к каталогу хранения изображений на диске.
 * <p>
 * Относительный {@code app.storage.base-path} приводится к абсолютному при старте приложения,
 * чтобы сохранение и чтение файлов не зависели от рабочей директории процесса.
 */
@Component
@Getter
public class StorageProperties {

    private static final Logger log = LoggerFactory.getLogger(StorageProperties.class);

    @Value("${app.storage.base-path:images}")
    private String basePath;

    private Path absoluteBasePath;

    @PostConstruct
    void init() {
        absoluteBasePath = Paths.get(basePath).toAbsolutePath().normalize();
        log.info("Каталог хранения изображений: {}", absoluteBasePath);
    }
}
