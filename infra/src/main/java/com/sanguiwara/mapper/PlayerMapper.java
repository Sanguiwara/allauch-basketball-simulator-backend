package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.entity.PlayerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        uses = {BadgeEntityMapper.class, EntityReferenceMapper.class, PlayerTemporaryModifierEntityMapper.class}
)
public interface PlayerMapper {

    @Mapping(target = "teamsID", source = "teams")
    @Mapping(target = "clubID", source = "club")
    @Mapping(target = "badgeIds", source = "badges")
    @Mapping(target = "temporaryModifiers", source = "temporaryModifiers")
    Player toDomain(PlayerEntity entity);

    @Mapping(target = "teams", source = "teamsID")
    @Mapping(target = "club", source = "clubID")
    @Mapping(target = "badges", source = "badgeIds")
    @Mapping(target = "temporaryModifiers", source = "temporaryModifiers")
    PlayerEntity toEntity(Player player);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teams", source = "teamsID")
    @Mapping(target = "club", source = "clubID")
    @Mapping(target = "badges", ignore = true)
    @Mapping(target = "temporaryModifiers", source = "temporaryModifiers")
    void updateEntity(Player player, @MappingTarget PlayerEntity entity);
}
