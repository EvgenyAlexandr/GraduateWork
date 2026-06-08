package ru.skypro.homework.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;

class CommentMapperTest {

    private CommentMapper commentMapper;

    @BeforeEach
    void setUp() {
        commentMapper = Mappers.getMapper(CommentMapper.class);
    }

    @Test
    @DisplayName("Маппинг сущности Comment в DTO сохраняет текст, автора и дату создания")
    void toDto_mapsCommentFields() {
        User author = new User();
        author.setId(2);
        author.setFirstName("Anna");
        author.setImage("/users/2/image");

        Ad ad = new Ad();
        ad.setPk(5);

        Comment comment = new Comment();
        comment.setPk(7);
        comment.setText("Comment text");
        comment.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));
        comment.setAuthor(author);
        comment.setAd(ad);

        ru.skypro.homework.dto.Comment dto = commentMapper.toDto(comment);

        assertThat(dto.getPk()).isEqualTo(7);
        assertThat(dto.getText()).isEqualTo("Comment text");
        assertThat(dto.getAuthor()).isEqualTo(2);
        assertThat(dto.getAuthorFirstName()).isEqualTo("Anna");
        assertThat(dto.getAuthorImage()).isEqualTo("/users/2/image");
        assertThat(dto.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Маппинг CreateOrUpdateComment в сущность Comment заполняет только текст")
    void toEntity_mapsCreateOrUpdateComment() {
        CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment();
        createOrUpdateComment.setText("New comment");

        Comment comment = commentMapper.toEntity(createOrUpdateComment);

        assertThat(comment.getText()).isEqualTo("New comment");
        assertThat(comment.getPk()).isNull();
        assertThat(comment.getAuthor()).isNull();
        assertThat(comment.getAd()).isNull();
    }
}
