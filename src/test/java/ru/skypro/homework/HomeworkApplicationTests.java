package ru.skypro.homework;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class HomeworkApplicationTests {

    @Test
    @DisplayName("Spring-контекст приложения успешно загружается")
    void contextLoads() {
    }

}
