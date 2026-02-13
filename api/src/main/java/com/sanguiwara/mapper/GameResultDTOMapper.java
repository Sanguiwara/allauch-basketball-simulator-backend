package com.sanguiwara.mapper;

import com.sanguiwara.dto.GameResultDTO;
import com.sanguiwara.result.GameResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameResultDTOMapper {

    GameResultDTO toDto(GameResult gameResult);
}