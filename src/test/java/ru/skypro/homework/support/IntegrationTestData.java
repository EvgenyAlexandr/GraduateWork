package ru.skypro.homework.support;

import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Общие тестовые данные для интеграционных тестов Этапа III.
 * <p>
 * Пароли и email соответствуют ограничениям OpenAPI; пароль хранится в BCrypt-хеше.
 */
public final class IntegrationTestData {

    /** Пароль тестовых пользователей (8+ символов по спецификации). */
    public static final String PASSWORD = "password1";

    /** Email обычного пользователя-владельца объявлений. */
    public static final String USER_EMAIL = "user@test.com";

    /** Email второго пользователя для проверки запрета доступа к чужим ресурсам. */
    public static final String OTHER_EMAIL = "other@test.com";

    /** Email администратора с правом редактировать любые объявления. */
    public static final String ADMIN_EMAIL = "admin@test.com";

    /** Телефон в формате, допустимом OpenAPI. */
    public static final String PHONE = "+79991234567";

    private IntegrationTestData() {
    }

    /**
     * Сохраняет пользователя в H2 с закодированным паролем.
     */
    public static User persistUser(UserRepository userRepository, PasswordEncoder passwordEncoder,
            String email, Role role, String firstName, String lastName) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(PASSWORD));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(PHONE);
        user.setRole(role);
        return userRepository.save(user);
    }

    /**
     * Сохраняет объявление с минимально валидными полями и указанным автором.
     */
    public static Ad persistAd(AdRepository adRepository, User author) {
        Ad ad = new Ad();
        ad.setTitle("Test title");
        ad.setDescription("Description of test ad");
        ad.setPrice(1_000);
        ad.setAuthor(author);
        ad.setImage("/ads/0/image");
        return adRepository.save(ad);
    }
}
