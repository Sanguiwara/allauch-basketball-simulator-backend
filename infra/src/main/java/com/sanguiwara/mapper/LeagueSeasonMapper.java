package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.LeagueSeason;
import com.sanguiwara.entity.LeagueSeasonEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {LeagueMapper.class, TeamMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LeagueSeasonMapper {

    // Teams mapping is complex (TeamForSeason). Ignore for now; only map league and year.
    @Mapping(target = "teams", ignore = true)
    LeagueSeasonEntity toEntity(LeagueSeason leagueSeason);

    @Mapping(target = "teams", ignore = true)
    LeagueSeason toDomain(LeagueSeasonEntity entity);
}
