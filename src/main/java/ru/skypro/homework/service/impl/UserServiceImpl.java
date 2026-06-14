package ru.skypro.homework.service.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
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
            userMapper.updateEntityFromDto(updateUser, user);
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
        user.setImage(imageStorageService.saveAvatarImage(image));
        return userRepository.save(user);
    }
}
