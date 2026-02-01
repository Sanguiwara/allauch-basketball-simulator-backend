package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.dto.GameDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameDTOMapper {



    // GamePlan id (tu ne veux que celui du home)
    @Mapping(target = "homeGamePlanId", source = "homeGamePlan.id")

    // Home side = ownerTeam du homeGamePlan
    @Mapping(target = "homeTeamId", source = "homeGamePlan.ownerTeam.id")
    @Mapping(target = "homeTeamName", source = "homeGamePlan.ownerTeam.name")

    // Away side = ownerTeam du awayGamePlan (ownerTeam = l’équipe qui “porte” ce plan)
    @Mapping(target = "awayTeamId", source = "awayGamePlan.ownerTeam.id")
    @Mapping(target = "awayTeamName", source = "awayGamePlan.ownerTeam.name")
    GameDTO toDto(Game game);

    Game toDomain(GameDTO gameDTO);
}
