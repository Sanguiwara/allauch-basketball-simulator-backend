package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.User;
import com.sanguiwara.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {ClubMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    User toDomain(UserEntity entity);

    @Mapping(target = "club", ignore = true)
    UserEntity toEntity(User domain);

    @Mapping(target = "club", ignore = true)
    void updateEntity(@MappingTarget UserEntity target, User source);


}

