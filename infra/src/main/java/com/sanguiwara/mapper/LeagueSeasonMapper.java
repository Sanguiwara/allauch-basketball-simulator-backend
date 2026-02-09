package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.LeagueSeason;
import com.sanguiwara.entity.LeagueSeasonEntity;
import jakarta.persistence.EntityManager;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {LeagueMapper.class, TeamSeasonMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LeagueSeasonMapper {

    LeagueSeasonEntity toEntity(LeagueSeason leagueSeason, @Context EntityManager em);

    LeagueSeason toDomain(LeagueSeasonEntity entity);
}
