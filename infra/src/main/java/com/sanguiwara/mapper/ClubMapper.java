package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Club;
import com.sanguiwara.entity.ClubEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    uses = {TeamMapper.class, PlayerMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ClubMapper {

    ClubEntity toEntity(Club club);

    @Mapping(target = "teams", source = "teams")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "players", ignore = true)
    Club toDomain(ClubEntity entity);
}
