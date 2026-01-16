package com.example.bankcards.mapper;


import com.example.bankcards.dto.card.response.CardMaskedResponse;
import com.example.bankcards.dto.card.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardMapper {

    @Mapping(target = "maskedNumber", source = "number", qualifiedByName = "maskNumber")
    @Mapping(target = "ownerName", source = "owner", qualifiedByName = "mapOwnerName")
    CardMaskedResponse toMaskedDto(Card card);

    @Mapping(target = "ownerName", source = "owner", qualifiedByName = "mapOwnerName")
    CardResponse toDto(Card card);

    @Named("maskNumber")
    default String maskNumber(String number) {
        if (number == null || number.length() < 4) {
            return "****";
        }
        return "**** **** **** " + number.substring(number.length() - 4);
    }

    @Named("mapOwnerName")
    default String mapOwnerName(User owner) {
        if (owner == null) {
            return "Unknown";
        }
        return String.format("%s %s %s",
                owner.getLastName(),
                owner.getFirstName(),
                owner.getSecondName()
        ).trim();
    }
}