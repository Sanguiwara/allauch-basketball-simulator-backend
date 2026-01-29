package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.TeamSeason;
import com.sanguiwara.entity.LeagueSeasonEntity;
import com.sanguiwara.entity.TeamEntity;
import com.sanguiwara.entity.TeamSeasonEntity;
import jakarta.persistence.EntityManager;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {TeamMapper.class})
public interface TeamSeasonMapper {


    @Mapping(target = "leagueSeasonId", source = "leagueSeason.id")
    TeamSeason toDomain(TeamSeasonEntity entity);

    @Mapping(target = "leagueSeason", source = "leagueSeasonId", qualifiedByName = "leagueSeasonRef")
    TeamSeasonEntity toEntity(TeamSeason domain, @Context EntityManager em);



    @Named("leagueSeasonRef")
    default LeagueSeasonEntity leagueSeasonRef(UUID id, @Context EntityManager em) {
        return em.getReference(LeagueSeasonEntity.class, id);
    }

}