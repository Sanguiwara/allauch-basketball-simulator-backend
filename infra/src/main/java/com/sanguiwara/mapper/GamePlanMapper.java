package com.sanguiwara.mapper;


import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.entity.GamePlanEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {InGamePlayerMapper.class, TeamMapper.class})
public interface GamePlanMapper {

     GamePlanEntity toEntity(GamePlan gamePlan);
     GamePlan toDomain(GamePlanEntity gamePlanEntity);

     // UPDATE: patch une entity existante (managée) sans toucher aux collections sensibles
     @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
     @Mapping(target = "activePlayers", ignore = true) // IMPORTANT
     @Mapping(target = "id", ignore = true)            // on ne change jamais l'id
     void updateEntity(@MappingTarget GamePlanEntity target, GamePlan source);

}
