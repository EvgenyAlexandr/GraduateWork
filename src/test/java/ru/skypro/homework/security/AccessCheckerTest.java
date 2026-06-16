package ru.skypro.homework.security;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.User;

/**
 * Unit-тесты {@link AccessChecker} — правило «владелец ресурса или ADMIN».
 */
class AccessCheckerTest {

    private AccessChecker accessChecker;
    private User owner;
    private User otherUser;
    private User admin;

    @BeforeEach
    void setUp() {
        accessChecker = new AccessChecker();
        owner = new User();
        owner.setId(1);
        owner.setRole(Role.USER);

        otherUser = new User();
        otherUser.setId(2);
        otherUser.setRole(Role.USER);

        admin = new User();
        admin.setId(3);
        admin.setRole(Role.ADMIN);
    }

    @Test
    @DisplayName("Владелец ресурса может выполнять операцию над своим объявлением или комментарием")
    void checkOwnerOrAdmin_allowsOwner() {
        assertThatCode(() -> accessChecker.checkOwnerOrAdmin(owner, owner))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ADMIN может выполнять операцию над чужим ресурсом")
    void checkOwnerOrAdmin_allowsAdmin() {
        assertThatCode(() -> accessChecker.checkOwnerOrAdmin(owner, admin))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Обычный пользователь не может изменять чужой ресурс — AccessDeniedException")
    void checkOwnerOrAdmin_deniesOtherUser() {
        assertThatThrownBy(() -> accessChecker.checkOwnerOrAdmin(owner, otherUser))
                .isInstanceOf(AccessDeniedException.class);
    }
}
