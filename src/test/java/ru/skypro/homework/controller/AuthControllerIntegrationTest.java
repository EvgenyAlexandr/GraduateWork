package ru.skypro.homework.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.support.IntegrationTestData;

/**
 * Интеграционные тесты регистрации и входа.
 * <p>
 * Проверяют сохранение пользователя в БД и коды ответов OpenAPI.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("POST /register создаёт пользователя в БД и возвращает 201 Created")
    void register_newUser_returnsCreated() throws Exception {
        String body = "{"
                + "\"username\":\"newuser@test.com\","
                + "\"password\":\"password1\","
                + "\"firstName\":\"Anna\","
                + "\"lastName\":\"Annova\","
                + "\"phone\":\"+79991234567\","
                + "\"role\":\"USER\""
                + "}";

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        org.assertj.core.api.Assertions.assertThat(userRepository.existsByEmail("newuser@test.com")).isTrue();
    }

    @Test
    @DisplayName("POST /register с занятым email возвращает 400 Bad Request")
    void register_duplicateEmail_returnsBadRequest() throws Exception {
        // Сначала регистрируем пользователя
        String body = "{"
                + "\"username\":\"dup@test.com\","
                + "\"password\":\"password1\","
                + "\"firstName\":\"Anna\","
                + "\"lastName\":\"Annova\","
                + "\"phone\":\"+79991234567\","
                + "\"role\":\"USER\""
                + "}";
        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        // Повторная регистрация с тем же email
        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /login с верными учётными данными возвращает 200 OK")
    void login_validCredentials_returnsOk() throws Exception {
        String registerBody = "{"
                + "\"username\":\"login@test.com\","
                + "\"password\":\"password1\","
                + "\"firstName\":\"Login\","
                + "\"lastName\":\"Userov\","
                + "\"phone\":\"+79991234567\","
                + "\"role\":\"USER\""
                + "}";
        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(registerBody))
                .andExpect(status().isCreated());

        String loginBody = "{\"username\":\"login@test.com\",\"password\":\"password1\"}";
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(loginBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /login с неверным паролем возвращает 401 Unauthorized")
    void login_wrongPassword_returnsUnauthorized() throws Exception {
        String registerBody = "{"
                + "\"username\":\"wrongpass@test.com\","
                + "\"password\":\"password1\","
                + "\"firstName\":\"Wrong\","
                + "\"lastName\":\"Passov\","
                + "\"phone\":\"+79991234567\","
                + "\"role\":\"USER\""
                + "}";
        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(registerBody))
                .andExpect(status().isCreated());

        String loginBody = "{\"username\":\"wrongpass@test.com\",\"password\":\"badpass1\"}";
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(loginBody))
                .andExpect(status().isUnauthorized());
    }
}
