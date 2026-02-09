package com.sanguiwara.mapper;

import com.sanguiwara.entity.GameResultEntity;
import com.sanguiwara.result.DriveResult;
import com.sanguiwara.result.ThreePointShootingResult;
import com.sanguiwara.result.TwoPointShootingResult;
import com.sanguiwara.result.GameResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GameResultMapper {

    GameResult toDomain(GameResultEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "game", ignore = true)
    GameResultEntity toEntity(GameResult gameResult);

    default ThreePointShootingResult toThreePointShootingResult(GameResultEntity.ShootingResultEmbeddable source) {
        if (source == null) return null;
        return new ThreePointShootingResult(source.getAttempts(), source.getMade(), List.of());
    }

    default TwoPointShootingResult toTwoPointShootingResult(GameResultEntity.ShootingResultEmbeddable source) {
        if (source == null) return null;
        return new TwoPointShootingResult(source.getAttempts(), source.getMade(), List.of());
    }

    default DriveResult toDriveResult(GameResultEntity.DriveResultEmbeddable source) {
        if (source == null) return null;
        return new DriveResult(source.getAttempts(), source.getMade(), source.getFoulsDrawn(), List.of());
    }

    default GameResultEntity.ShootingResultEmbeddable toShootingResultEmbeddable(ThreePointShootingResult source) {
        if (source == null) return null;
        var out = new GameResultEntity.ShootingResultEmbeddable();
        out.setAttempts(source.attempts());
        out.setMade(source.made());
        return out;
    }

    default GameResultEntity.ShootingResultEmbeddable toShootingResultEmbeddable(TwoPointShootingResult source) {
        if (source == null) return null;
        var out = new GameResultEntity.ShootingResultEmbeddable();
        out.setAttempts(source.attempts());
        out.setMade(source.made());
        return out;
    }

    default GameResultEntity.DriveResultEmbeddable toDriveResultEmbeddable(DriveResult source) {
        if (source == null) return null;
        var out = new GameResultEntity.DriveResultEmbeddable();
        out.setAttempts(source.attempts());
        out.setMade(source.made());
        out.setFoulsDrawn(source.foulsDrawn());
        return out;
    }
}

