package ru.skypro.homework.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.support.IntegrationTestData;

/**
 * Интеграционные тесты {@link CommentService} / {@link ru.skypro.homework.service.impl.CommentServiceImpl}.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User owner;
    private Ad ad;

    @BeforeEach
    void setUp() {
        owner = IntegrationTestData.persistUser(
                userRepository, passwordEncoder,
                IntegrationTestData.USER_EMAIL, Role.USER, "Ivan", "Ivanov");
        IntegrationTestData.persistUser(
                userRepository, passwordEncoder,
                IntegrationTestData.OTHER_EMAIL, Role.USER, "Petr", "Petrov");
        ad = IntegrationTestData.persistAd(adRepository, owner);

        CreateOrUpdateComment text = new CreateOrUpdateComment();
        text.setText("Comment from owner");
        commentService.createComment(text, ad, owner);
    }

    @Test
    @DisplayName("getComments возвращает комментарии, связанные с объявлением")
    void getComments_returnsCommentsForAd() {
        assertThat(commentService.getComments(ad.getPk()).getCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("deleteComment чужим пользователем выбрасывает AccessDeniedException")
    void deleteComment_byStranger_throwsAccessDenied() {
        Integer commentId = commentService.getComments(ad.getPk()).getResults().get(0).getPk();

        assertThatThrownBy(() -> commentService.deleteComment(
                ad.getPk(), commentId, IntegrationTestData.OTHER_EMAIL))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("deleteComment автором удаляет запись из БД")
    void deleteComment_byOwner_removesComment() {
        Integer commentId = commentService.getComments(ad.getPk()).getResults().get(0).getPk();

        commentService.deleteComment(ad.getPk(), commentId, IntegrationTestData.USER_EMAIL);

        assertThat(commentRepository.findById(commentId)).isEmpty();
    }

    @Test
    @DisplayName("addComment создаёт комментарий и возвращает DTO")
    void addComment_persistsComment() {
        CreateOrUpdateComment dto = new CreateOrUpdateComment();
        dto.setText("Another comment");

        ru.skypro.homework.dto.Comment created =
                commentService.addComment(ad.getPk(), dto, IntegrationTestData.USER_EMAIL);

        assertThat(created.getPk()).isNotNull();
        assertThat(commentService.getComments(ad.getPk()).getCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("updateComment изменяет текст комментария")
    void updateComment_byOwner_updatesText() {
        Integer commentId = commentService.getComments(ad.getPk()).getResults().get(0).getPk();
        CreateOrUpdateComment newText = new CreateOrUpdateComment();
        newText.setText("Updated comment");

        ru.skypro.homework.dto.Comment updated = commentService.updateComment(
                ad.getPk(), commentId, newText, IntegrationTestData.USER_EMAIL);

        assertThat(updated.getText()).isEqualTo("Updated comment");
    }
}
