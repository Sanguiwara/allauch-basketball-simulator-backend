package com.sanguiwara.mapper;

import com.sanguiwara.entity.PlayerTemporaryModifierEmbeddable;
import com.sanguiwara.modifiers.PlayerModifier;
import org.mapstruct.Mapper;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PlayerTemporaryModifierEntityMapper {

    PlayerModifier toDomain(PlayerTemporaryModifierEmbeddable entity);

    PlayerTemporaryModifierEmbeddable toEntity(PlayerModifier modifier);

    default Set<PlayerModifier> toDomainSet(Set<PlayerTemporaryModifierEmbeddable> entities) {
        if (entities == null || entities.isEmpty()) return new HashSet<>();

        Set<PlayerModifier> modifiers = new HashSet<>();
        for (PlayerTemporaryModifierEmbeddable entity : entities) {
            PlayerModifier modifier = toDomain(entity);
            if (modifier != null) modifiers.add(modifier);
        }
        return modifiers;
    }

    default Set<PlayerTemporaryModifierEmbeddable> toEntitySet(Set<PlayerModifier> modifiers) {
        if (modifiers == null || modifiers.isEmpty()) return new HashSet<>();

        Set<PlayerTemporaryModifierEmbeddable> entities = new HashSet<>();
        for (PlayerModifier modifier : modifiers) {
            PlayerTemporaryModifierEmbeddable entity = toEntity(modifier);
            if (entity != null) entities.add(entity);
        }
        return entities;
    }
}
