package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Training;
import com.sanguiwara.entity.TrainingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {EntityReferenceMapper.class, TeamMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainingMapper {

    @Mapping(target = "team", source = "team", qualifiedByName = "teamRefFromDomain")
    TrainingEntity toEntity(Training training);

    Training toDomain(TrainingEntity entity);
}

