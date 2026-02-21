package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Training;
import com.sanguiwara.entity.TeamEntity;
import com.sanguiwara.entity.TrainingEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {TeamMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainingMapper {

    @Mapping(target = "team", ignore = true)
    TrainingEntity toEntity(Training training);

    Training toDomain(TrainingEntity entity);

    @AfterMapping
    default void linkTeam(Training training, @MappingTarget TrainingEntity entity) {
        if (training.getTeam() == null || training.getTeam().getId() == null) {
            entity.setTeam(null);
            return;
        }
        TeamEntity team = new TeamEntity();
        team.setId(training.getTeam().getId());
        entity.setTeam(team);
    }
}

