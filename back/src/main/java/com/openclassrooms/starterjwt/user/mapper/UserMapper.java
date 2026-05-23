package com.openclassrooms.starterjwt.user.mapper;

import com.openclassrooms.starterjwt.shared.mapper.EntityMapper;
import com.openclassrooms.starterjwt.user.dto.UserDto;
import com.openclassrooms.starterjwt.user.model.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserDto, User> {
}
