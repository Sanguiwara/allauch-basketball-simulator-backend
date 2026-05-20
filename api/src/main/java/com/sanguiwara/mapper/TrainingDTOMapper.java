package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Training;
import com.sanguiwara.dto.TrainingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TeamDTOMapper.class, PlayerProgressionDTOMapper.class, TrainingProgressionDTOMapper.class})
public interface TrainingDTOMapper {

    @Mapping(target = "trainingType", source = "trainingType")
    @Mapping(target = "trainingProgression", source = "trainingProgression")
    @Mapping(target = "team", source = "team")
    @Mapping(target = "playerProgressions", source = "playerProgressions")
    TrainingDTO toDto(Training training);
}

