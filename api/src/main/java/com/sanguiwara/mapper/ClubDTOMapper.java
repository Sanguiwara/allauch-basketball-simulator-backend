package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Club;
import com.sanguiwara.dto.ClubDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TeamDTOMapper.class, PlayerDTOMapper.class})
public interface ClubDTOMapper {

    ClubDTO toDto(Club club);

    default Club toDomain(ClubDTO clubDTO) {
        if (clubDTO == null) {
            return null;
        }
        Club club = new Club(clubDTO.name());
        club.setId(clubDTO.id());
        return club;
    }
}
