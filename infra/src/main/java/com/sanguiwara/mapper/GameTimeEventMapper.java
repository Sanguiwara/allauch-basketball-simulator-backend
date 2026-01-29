package com.sanguiwara.mapper;

import com.sanguiwara.entity.GameTimeEventEntity;
import com.sanguiwara.executor.GameExecutor;
import com.sanguiwara.timeevent.GameTimeEvent;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GameTimeEventMapper {

    // Persistable part only (no executor)
    GameTimeEventEntity toEntity(GameTimeEvent event);

    // Domain needs an executor to be runnable
    default GameTimeEvent toDomain(GameTimeEventEntity entity, @Context GameExecutor executor) {
        if (entity == null) return null;
        return new GameTimeEvent(
                entity.getId(),
                entity.getExecuteAt(),
                entity.getGameId(),
                executor
        );
    }
}
