package com.sanguiwara.mapper;


import com.sanguiwara.entity.GameEntity;
import com.sanguiwara.baserecords.Game;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface GameMapper {

    Game toDomain(GameEntity entity);


    GameEntity toEntity(Game player);
}
