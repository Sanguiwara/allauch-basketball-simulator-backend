package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.entity.ClubEntity;
import com.sanguiwara.entity.LeagueSeasonEntity;
import com.sanguiwara.entity.PlayerEntity;
import com.sanguiwara.entity.PlayerSeasonSnapshotEntity;
import com.sanguiwara.entity.PlayerSeasonSnapshotId;
import com.sanguiwara.progression.PlayerSeasonSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class PlayerSeasonSnapshotMapper {

    public PlayerSeasonSnapshot toDomain(PlayerSeasonSnapshotEntity entity) {
        if (entity == null) {
            return null;
        }

        return new PlayerSeasonSnapshot(entity.getId().getLeagueSeasonId(), toPlayer(entity));
    }

    public PlayerSeasonSnapshotEntity toEntity(PlayerSeasonSnapshot snapshot) {
        Player player = snapshot.player();
        UUID leagueSeasonId = snapshot.leagueSeasonId();
        UUID playerId = player.getId();

        PlayerSeasonSnapshotEntity entity = toEntity(player);
        entity.setId(new PlayerSeasonSnapshotId(leagueSeasonId, playerId));
        entity.setLeagueSeason(leagueSeasonRef(leagueSeasonId));
        entity.setPlayer(playerRef(playerId));
        entity.setClub(clubRef(player.getClubID()));
        return entity;
    }

    @Mapping(target = "id", source = "id.playerId")
    @Mapping(target = "clubID", source = "club.id")
    @Mapping(target = "teamsID", expression = "java(new java.util.HashSet<>())")
    @Mapping(target = "badgeIds", source = "badgeIds")
    @Mapping(target = "temporaryModifiers", expression = "java(new java.util.HashSet<>())")
    protected abstract Player toPlayer(PlayerSeasonSnapshotEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "leagueSeason", ignore = true)
    @Mapping(target = "player", ignore = true)
    @Mapping(target = "club", ignore = true)
    @Mapping(target = "badgeIds", source = "badgeIds")
    protected abstract PlayerSeasonSnapshotEntity toEntity(Player player);

    private LeagueSeasonEntity leagueSeasonRef(UUID id) {
        LeagueSeasonEntity leagueSeason = new LeagueSeasonEntity();
        leagueSeason.setId(id);
        return leagueSeason;
    }

    private PlayerEntity playerRef(UUID id) {
        PlayerEntity player = new PlayerEntity();
        player.setId(id);
        return player;
    }

    private ClubEntity clubRef(UUID id) {
        if (id == null) {
            return null;
        }
        ClubEntity club = new ClubEntity();
        club.setId(id);
        return club;
    }
}
