package com.example.bankcards.mapper;

import com.example.bankcards.dto.user.response.UserResponse;
import com.example.bankcards.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "FIO", source = ".", qualifiedByName = "concatFio")
    UserResponse toDto(User user);


    @Named("concatFio")
    default String concatFio(User user) {
        if (user == null) return null;
        return String.format("%s %s %s",
                user.getLastName(),
                user.getFirstName(),
                user.getSecondName()
        ).trim();
    }
}