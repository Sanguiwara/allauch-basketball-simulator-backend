package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.dto.GamePlanDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PlayerIdMapper.class, InGamePlayerDTOMapper.class})
public interface GamePlanDTOMapper {


    GamePlanDTO toDTO(GamePlan gamePlan);

    GamePlan toDomain(GamePlanDTO gamePlanEntity);


}
