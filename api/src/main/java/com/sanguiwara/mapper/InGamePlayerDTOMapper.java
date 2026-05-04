package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.dto.InGamePlayerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PlayerDTOMapper.class})
public interface InGamePlayerDTOMapper {
    InGamePlayerDTO toDto(InGamePlayer inGamePlayer);

    @Mapping(target = "matchRating", ignore = true)
    @Mapping(target = "threePtScore", ignore = true)
    @Mapping(target = "threePtDefenseScore", ignore = true)
    @Mapping(target = "twoPtScore", ignore = true)
    @Mapping(target = "twoPtDefenseScore", ignore = true)
    @Mapping(target = "driveScore", ignore = true)
    @Mapping(target = "driveDefenseScore", ignore = true)
    @Mapping(target = "manToManPlaymakingOffScore", ignore = true)
    @Mapping(target = "manToManPlaymakingDefScore", ignore = true)
    @Mapping(target = "zonePlaymakingOffScore", ignore = true)
    @Mapping(target = "zonePlaymakingDefScore", ignore = true)
    @Mapping(target = "zone23DefenseScore", ignore = true)
    @Mapping(target = "zone32DefenseScore", ignore = true)
    @Mapping(target = "zone212DefenseScore", ignore = true)
    @Mapping(target = "reboundScore", ignore = true)
    @Mapping(target = "stealScore", ignore = true)
    InGamePlayer toDomain(InGamePlayerDTO inGamePlayerDTO);
}

