package com.sanguiwara.mapper;

import com.sanguiwara.dto.TrainingTemporaryModifierDTO;
import com.sanguiwara.modifiers.PlayerModifier;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface TemporaryModifierDTOMapper {

    TrainingTemporaryModifierDTO toDto(PlayerModifier modifier);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<TrainingTemporaryModifierDTO> toDtoList(Set<PlayerModifier> modifiers);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<TrainingTemporaryModifierDTO> toDtoList(List<PlayerModifier> modifiers);
}
