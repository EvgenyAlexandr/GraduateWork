package ru.skypro.homework.service.impl;

import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import ru.skypro.homework.constant.ApiConstants;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageStorageService;
import ru.skypro.homework.service.UserService;

/**
 * Реализация {@link UserService}: работа с пользователями через {@link UserRepository}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final Pattern PHONE_PATTERN = Pattern.compile(ApiConstants.PHONE_PATTERN);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageStorageService imageStorageService;

    /** {@inheritDoc} */
    @Override
    public Optional<ru.skypro.homework.dto.User> getUserDto(Integer id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<ru.skypro.homework.dto.User> getUserDtoByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toDto);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<User> findEntityByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<User> findEntityById(Integer id) {
        return userRepository.findById(id);
    }

    /** {@inheritDoc} */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public User createUser(Register register) {
        User user = userMapper.toEntity(register);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Optional<User> updateUser(Integer id, UpdateUser updateUser) {
        return userRepository.findById(id).map(user -> {
            UpdateUser merged = mergeUpdateUser(updateUser, user);
            validateUpdateUser(merged);
            userMapper.updateEntityFromDto(merged, user);
            return userRepository.save(user);
        });
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public boolean changePassword(String email, String currentPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return false;
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    /** {@inheritDoc} */
    @Override
    public ru.skypro.homework.dto.User toDto(User user) {
        return userMapper.toDto(user);
    }

    /** {@inheritDoc} */
    @Override
    public UpdateUser toUpdateUserDto(User user) {
        UpdateUser updateUser = new UpdateUser();
        updateUser.setFirstName(user.getFirstName());
        updateUser.setLastName(user.getLastName());
        updateUser.setPhone(user.getPhone());
        return updateUser;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public User updateUserImage(Integer id, MultipartFile image) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + id));
        imageStorageService.deleteByPublicUrl(user.getImage());
        user.setImage(imageStorageService.saveAvatarImage(image));
        return userRepository.save(user);
    }

    /**
     * Дополняет DTO текущими значениями из БД, если фронтенд передал не все поля.
     */
    private UpdateUser mergeUpdateUser(UpdateUser updateUser, User user) {
        UpdateUser merged = new UpdateUser();
        merged.setFirstName(trimToNull(
                updateUser.getFirstName() != null ? updateUser.getFirstName() : user.getFirstName()));
        merged.setLastName(trimToNull(
                updateUser.getLastName() != null ? updateUser.getLastName() : user.getLastName()));
        merged.setPhone(normalizePhone(
                updateUser.getPhone() != null ? updateUser.getPhone() : user.getPhone()));
        return merged;
    }

    /**
     * Проверяет итоговые значения профиля после слияния с данными из БД.
     */
    private void validateUpdateUser(UpdateUser updateUser) {
        validateName(updateUser.getFirstName(), "имя",
                ApiConstants.FIRST_NAME_MIN_LENGTH, ApiConstants.UPDATE_FIRST_NAME_MAX_LENGTH);
        validateName(updateUser.getLastName(), "фамилия",
                ApiConstants.LAST_NAME_MIN_LENGTH, ApiConstants.UPDATE_LAST_NAME_MAX_LENGTH);
        validatePhone(updateUser.getPhone());
    }

    private void validateName(String value, String fieldName, int minLength, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Поле «" + fieldName + "» не может быть пустым");
        }
        if (value.length() < minLength || value.length() > maxLength) {
            throw new IllegalArgumentException(
                    "Поле «" + fieldName + "» должно содержать от " + minLength + " до " + maxLength + " символов");
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Телефон не может быть пустым");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("Некорректный формат телефона");
        }
    }

    /**
     * Приводит телефон к формату {@code +79991234567}, понятному фронтенду и regex OpenAPI.
     */
    private String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        String trimmed = phone.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }
        String digitsOnly = trimmed.replaceAll("[\\s\\-()]", "");
        if (digitsOnly.startsWith("8") && digitsOnly.length() == 11) {
            return "+7" + digitsOnly.substring(1);
        }
        if (digitsOnly.startsWith("7") && digitsOnly.length() == 11) {
            return "+" + digitsOnly;
        }
        return digitsOnly;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
