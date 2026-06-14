package ru.skypro.homework.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import ru.skypro.homework.entity.User;

/**
 * Обёртка над сущностью {@link User} для интеграции с Spring Security.
 * <p>
 * Логином ({@link #getUsername()}) выступает email пользователя из БД.
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    /**
     * @param user сущность пользователя из БД
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /** {@inheritDoc} — возвращает email пользователя. */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /** {@inheritDoc} — возвращает BCrypt-хеш пароля из БД. */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Возвращает роль пользователя в формате {@code ROLE_USER} / {@code ROLE_ADMIN}.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
