package ru.skypro.homework.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.Role;

/**
 * Unit-тесты {@link ru.skypro.homework.mapper.UserMapper}.
 */
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    @DisplayName("Маппинг сущности User в DTO сохраняет все поля")
    void toDto_mapsUserFields() {
        ru.skypro.homework.entity.User user = new ru.skypro.homework.entity.User();
        user.setId(1);
        user.setEmail("user@test.com");
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setPhone("+79991234567");
        user.setRole(Role.USER);
        user.setImage("/users/1/image");

        ru.skypro.homework.dto.User dto = userMapper.toDto(user);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getEmail()).isEqualTo("user@test.com");
        assertThat(dto.getFirstName()).isEqualTo("Ivan");
        assertThat(dto.getRole()).isEqualTo(Role.USER);
        assertThat(dto.getImage()).isEqualTo("/users/1/image");
    }

    @Test
    @DisplayName("Маппинг Register в сущность User: username преобразуется в email")
    void toEntity_mapsRegisterUsernameToEmail() {
        Register register = new Register();
        register.setUsername("newuser@test.com");
        register.setPassword("password1");
        register.setFirstName("Petr");
        register.setLastName("Petrov");
        register.setPhone("+79990001122");
        register.setRole(Role.USER);

        ru.skypro.homework.entity.User user = userMapper.toEntity(register);

        assertThat(user.getEmail()).isEqualTo("newuser@test.com");
        assertThat(user.getPassword()).isEqualTo("password1");
        assertThat(user.getFirstName()).isEqualTo("Petr");
        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.getId()).isNull();
    }
}
