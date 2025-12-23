package com.sanguiwara.mapper;

import baserecords.Player;
import com.sanguiwara.entity.PlayerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    Player toDomain(PlayerEntity entity);


}
