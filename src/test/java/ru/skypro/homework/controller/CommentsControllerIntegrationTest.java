package ru.skypro.homework.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CommentService;
import ru.skypro.homework.support.IntegrationTestData;

/**
 * Интеграционные тесты {@link CommentsController} через MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CommentsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Ad ad;
    private Integer commentId;

    @BeforeEach
    void setUp() {
        User owner = IntegrationTestData.persistUser(
                userRepository, passwordEncoder,
                IntegrationTestData.USER_EMAIL, Role.USER, "Ivan", "Ivanov");
        IntegrationTestData.persistUser(
                userRepository, passwordEncoder,
                IntegrationTestData.OTHER_EMAIL, Role.USER, "Petr", "Petrov");
        ad = IntegrationTestData.persistAd(adRepository, owner);

        CreateOrUpdateComment text = new CreateOrUpdateComment();
        text.setText("Comment from owner");
        commentId = commentService.createComment(text, ad, owner).getPk();
    }

    @Test
    @DisplayName("GET /ads/{id}/comments с авторизацией возвращает список комментариев")
    void getComments_withAuth_returnsOk() throws Exception {
        mockMvc.perform(get("/ads/" + ad.getPk() + "/comments")
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    @DisplayName("DELETE чужого комментария возвращает 403 Forbidden")
    void deleteComment_byOtherUser_returnsForbidden() throws Exception {
        mockMvc.perform(delete("/ads/" + ad.getPk() + "/comments/" + commentId)
                        .with(httpBasic(IntegrationTestData.OTHER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /ads/{id}/comments создаёт комментарий и возвращает 200")
    void addComment_withAuth_returnsOk() throws Exception {
        String body = "{\"text\":\"New comment text\"}";

        mockMvc.perform(post("/ads/" + ad.getPk() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("New comment text"));
    }

    @Test
    @DisplayName("PATCH комментария автором обновляет текст")
    void updateComment_byOwner_returnsOk() throws Exception {
        String body = "{\"text\":\"Patched comment\"}";

        mockMvc.perform(patch("/ads/" + ad.getPk() + "/comments/" + commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(httpBasic(IntegrationTestData.USER_EMAIL, IntegrationTestData.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Patched comment"));
    }
}
