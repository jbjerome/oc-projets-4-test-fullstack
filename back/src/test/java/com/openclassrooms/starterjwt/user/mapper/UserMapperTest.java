package com.openclassrooms.starterjwt.user.mapper;

import com.openclassrooms.starterjwt.user.dto.UserDto;
import com.openclassrooms.starterjwt.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserMapper - tests unitaires")
class UserMapperTest {

    private final UserMapper mapper = new UserMapperImpl();

    private User user() {
        return User.builder().id(1L).email("user@test.com").firstName("John")
                .lastName("Doe").password("pwd").admin(true).build();
    }

    private UserDto dto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setEmail("user@test.com");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPassword("pwd");
        dto.setAdmin(true);
        return dto;
    }

    @Test
    @DisplayName("toDto mappe l'entité")
    void toDto_mapsEntity() {
        UserDto result = mapper.toDto(user());

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("user@test.com");
        assertThat(result.isAdmin()).isTrue();
    }

    @Test
    @DisplayName("toEntity mappe le DTO")
    void toEntity_mapsDto() {
        User result = mapper.toEntity(dto());

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("user@test.com");
        assertThat(result.isAdmin()).isTrue();
    }

    @Test
    @DisplayName("mappe les listes dans les deux sens")
    void mapsLists() {
        assertThat(mapper.toDto(List.of(user()))).hasSize(1);
        assertThat(mapper.toEntity(List.of(dto()))).hasSize(1);
    }

    @Test
    @DisplayName("renvoie null pour des entrées null")
    void nullInputs_returnNull() {
        assertThat(mapper.toDto((User) null)).isNull();
        assertThat(mapper.toEntity((UserDto) null)).isNull();
        assertThat(mapper.toDto((List<User>) null)).isNull();
        assertThat(mapper.toEntity((List<UserDto>) null)).isNull();
    }
}
