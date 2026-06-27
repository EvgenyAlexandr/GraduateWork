package ru.skypro.homework.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.skypro.homework.repository.UserRepository;

/**
 * Загрузка пользователей из PostgreSQL для Basic Auth.
 * <p>
 * Заменяет {@code InMemoryUserDetailsManager}: данные берутся через {@link UserRepository},
 * который работает с {@code DataSource} приложения.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Ищет пользователя по email (логину) и формирует {@link CustomUserDetails}.
     *
     * @param username email пользователя
     * @throws UsernameNotFoundException если пользователь не найден в БД
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
    }
}
