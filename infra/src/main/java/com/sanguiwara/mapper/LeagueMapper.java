package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.League;
import com.sanguiwara.entity.LeagueEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LeagueMapper {
    LeagueEntity toEntity(League league);
    League toDomain(LeagueEntity entity);
}
