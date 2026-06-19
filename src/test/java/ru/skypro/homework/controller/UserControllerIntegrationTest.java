package ru.skypro.homework.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.support.IntegrationTestData;

/**
 * Интеграционные тесты контроллера профиля пользователя.
 * <p>
 * Проверяют смену пароля с BCrypt и получение данных текущего пользователя из БД.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        user = IntegrationTestData.persistUser(
                userRepository, passwordEncoder,
                IntegrationTestData.USER_EMAIL, Role.USER, "Ivan", "Ivanov");
    }

    @Test
    @DisplayName("GET /users/me возвращает профиль авторизованного пользователя из БД")
    void getMe_returnsCurrentUser() throws Exception {
        mockMvc.perform(get("/users/me")
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(IntegrationTestData.USER_EMAIL))
                .andExpect(jsonPath("$.firstName").value("Ivan"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("POST /users/set_password с верным текущим паролем возвращает 200 OK")
    void setPassword_withCorrectOldPassword_returnsOk() throws Exception {
        String body = "{\"currentPassword\":\"password1\",\"newPassword\":\"newpass123\"}";

        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isOk());

        // Новый пароль сохранён как BCrypt-хеш, не открытым текстом
        User updated = userRepository.findByEmail(IntegrationTestData.USER_EMAIL).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(passwordEncoder.matches("newpass123", updated.getPassword()))
                .isTrue();
    }

    @Test
    @DisplayName("PATCH /users/me обновляет имя и фамилию пользователя")
    void updateUser_changesFirstAndLastName_returnsOk() throws Exception {
        String body = "{\"firstName\":\"Petr\",\"lastName\":\"Petrov\",\"phone\":\"+79991234567\"}";

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Petr"))
                .andExpect(jsonPath("$.lastName").value("Petrov"));
    }

    @Test
    @DisplayName("PATCH /users/me нормализует телефон с пробелами и скобками")
    void updateUser_normalizesPhoneFormat_returnsOk() throws Exception {
        String body = "{\"firstName\":\"Ivan\",\"lastName\":\"Ivanov\",\"phone\":\"+7 (999) 123-45-67\"}";

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("+79991234567"));
    }

    @Test
    @DisplayName("PATCH /users/me с данными профиля на кириллице возвращает 200 OK")
    void updateUser_withCyrillicProfile_returnsOk() throws Exception {
        user.setFirstName("Стиляга");
        user.setLastName("Московский");
        user.setPhone("+79000000000");
        userRepository.save(user);

        String body = "{\"firstName\":\"Стиляга\",\"lastName\":\"Московский\",\"phone\":\"+79000000000\"}";

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Стиляга"))
                .andExpect(jsonPath("$.lastName").value("Московский"))
                .andExpect(jsonPath("$.phone").value("+79000000000"));
    }

    @Test
    @DisplayName("PATCH /users/me обновляет только телефон при коротком имени из регистрации в БД")
    void updateUser_updatePhoneOnly_keepsTwoCharNameFromRegistration_returnsOk() throws Exception {
        user.setFirstName("Al");
        user.setLastName("Po");
        userRepository.save(user);

        String body = "{\"phone\":\"+79991234567\"}";

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Al"))
                .andExpect(jsonPath("$.lastName").value("Po"))
                .andExpect(jsonPath("$.phone").value("+79991234567"));
    }

    @Test
    @DisplayName("PATCH /users/me с явно переданной фамилией короче 3 символов возвращает 400")
    void updateUser_withTooShortLastName_returnsBadRequest() throws Exception {
        String body = "{\"firstName\":\"Petr\",\"lastName\":\"Po\",\"phone\":\"+79991234567\"}";

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /users/me принимает имя длиной до 10 символов (лимит UpdateUser)")
    void updateUser_withMaxLengthName_returnsOk() throws Exception {
        String body = "{\"firstName\":\"Christophe\",\"lastName\":\"Ivanov\",\"phone\":\"+79991234567\"}";

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Christophe"));
    }

    @Test
    @DisplayName("PATCH /users/me с именем длиннее 10 символов возвращает 400")
    void updateUser_withTooLongName_returnsBadRequest() throws Exception {
        String body = "{\"firstName\":\"Александрович\",\"lastName\":\"Ivanov\",\"phone\":\"+79991234567\"}";

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /users/me с пустым телефоном в теле запроса берёт номер из БД")
    void updateUser_withBlankPhone_usesStoredPhone_returnsOk() throws Exception {
        String body = "{\"firstName\":\"Petr\",\"lastName\":\"Petrov\",\"phone\":\"\"}";

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value(IntegrationTestData.PHONE));
    }

    @Test
    @DisplayName("PATCH /users/me принимает телефон с пробелом между последними группами цифр")
    void updateUser_withSpacedPhoneFormat_returnsOk() throws Exception {
        String body = "{\"firstName\":\"Ivan\",\"lastName\":\"Ivanov\",\"phone\":\"+7(999)123 45 67\"}";

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("+79991234567"));
    }

    @Test
    @DisplayName("POST /users/set_password с неверным текущим паролем возвращает 400 Bad Request")
    void setPassword_withWrongOldPassword_returnsBadRequest() throws Exception {
        String body = "{\"currentPassword\":\"wrongpass\",\"newPassword\":\"newpass123\"}";

        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isBadRequest());
    }
}
