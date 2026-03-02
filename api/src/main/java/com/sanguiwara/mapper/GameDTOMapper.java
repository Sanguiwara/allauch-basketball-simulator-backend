package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.dto.GameDTO;
import com.sanguiwara.dto.SimplifiedGameDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {GameResultDTOMapper.class, InGamePlayerDTOMapper.class, PlayerProgressionDTOMapper.class})
public interface GameDTOMapper {



    // GamePlan id (tu ne veux que celui du home)
    @Mapping(target = "homeGamePlanId", source = "homeGamePlan.id")
    @Mapping(target = "awayGamePlanId", source = "awayGamePlan.id")


    // Home side = ownerTeam du homeGamePlan
    @Mapping(target = "homeTeamId", source = "homeGamePlan.ownerTeam.id")
    @Mapping(target = "homeTeamName", source = "homeGamePlan.ownerTeam.name")

    // Away side = ownerTeam du awayGamePlan (ownerTeam = l’équipe qui “porte” ce plan)
    @Mapping(target = "awayTeamId", source = "awayGamePlan.ownerTeam.id")
    @Mapping(target = "awayTeamName", source = "awayGamePlan.ownerTeam.name")
    @Mapping(target = "homeClubID", source = "homeGamePlan.ownerTeam.clubID")
    @Mapping(target = "awayClubID", source = "awayGamePlan.ownerTeam.clubID")
    @Mapping(target = "gameResult", source = "gameResult")
    @Mapping(target = "homeActivePlayers", source = "homeGamePlan.activePlayers")
    @Mapping(target = "awayActivePlayers", source = "awayGamePlan.activePlayers")
    @Mapping(target = "playerProgressions", source = "playerProgressions")

    GameDTO toDto(Game game);

    @Mapping(target = "homeGamePlanId", source = "homeGamePlan.id")
    @Mapping(target = "awayGamePlanId", source = "awayGamePlan.id")
    @Mapping(target = "homeTeamId", source = "homeGamePlan.ownerTeam.id")
    @Mapping(target = "homeTeamName", source = "homeGamePlan.ownerTeam.name")
    @Mapping(target = "awayTeamId", source = "awayGamePlan.ownerTeam.id")
    @Mapping(target = "awayTeamName", source = "awayGamePlan.ownerTeam.name")
    @Mapping(target = "homeClubID", source = "homeGamePlan.ownerTeam.clubID")
    @Mapping(target = "awayClubID", source = "awayGamePlan.ownerTeam.clubID")
    @Mapping(target = "gameResult", source = "gameResult")
    SimplifiedGameDTO toSimplifiedDto(Game game);

}
