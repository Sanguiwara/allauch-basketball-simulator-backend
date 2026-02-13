package com.sanguiwara.mapper;


import com.sanguiwara.baserecords.Game;
import com.sanguiwara.entity.GameEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {GamePlanMapper.class, GameResultMapper.class})

public interface GameMapper {

    Game toDomain(GameEntity entity);


    GameEntity toEntity(Game player);


    @AfterMapping
    default void linkGameResult(@MappingTarget GameEntity entity) {
        if (entity.getGameResult() != null) {
            entity.getGameResult().setGame(entity);
        }
    }
}
