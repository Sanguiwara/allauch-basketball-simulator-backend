package com.sanguiwara.mapper;


import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.entity.GamePlanEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GamePlanMapper {

     GamePlanEntity toEntity(GamePlan gamePlan);
     GamePlan toDomain(GamePlanEntity gamePlanEntity);
}
