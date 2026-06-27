package ru.skypro.homework.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.User;

/**
 * Проверка прав на изменение и удаление ресурсов.
 * <p>
 * ADMIN может редактировать любые объявления и комментарии; USER — только свои.
 */
@Component
public class AccessChecker {

    /**
     * Проверяет, что текущий пользователь — владелец ресурса или администратор.
     *
     * @param owner       автор объявления или комментария
     * @param currentUser текущий аутентифицированный пользователь
     * @throws AccessDeniedException если пользователь не владелец и не ADMIN
     */
    public void checkOwnerOrAdmin(User owner, User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        if (!owner.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Недостаточно прав для выполнения операции");
        }
    }
}
