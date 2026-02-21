package com.sanguiwara.mapper;

import com.sanguiwara.entity.TrainingTimeEventEntity;
import com.sanguiwara.executor.TrainingExecutor;
import com.sanguiwara.timeevent.TrainingTimeEvent;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainingTimeEventMapper {

    TrainingTimeEventEntity toEntity(TrainingTimeEvent event);

    default TrainingTimeEvent toDomain(TrainingTimeEventEntity entity, @Context TrainingExecutor trainingExecutor) {
        if (entity == null) return null;
        return new TrainingTimeEvent(
                entity.getId(),
                entity.getExecuteAt(),
                entity.getTrainingId(),
                trainingExecutor
        );
    }
}

