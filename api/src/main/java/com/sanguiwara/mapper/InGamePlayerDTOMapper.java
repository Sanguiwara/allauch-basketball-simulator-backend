package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.dto.InGamePlayerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PlayerDTOMapper.class})
public interface InGamePlayerDTOMapper {

    InGamePlayerDTO toDto(InGamePlayer inGamePlayer);

    InGamePlayer toDomain(InGamePlayerDTO inGamePlayerDTO);
}

