package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.AgeCategory;
import com.sanguiwara.baserecords.DefenseType;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.Gender;
import com.sanguiwara.baserecords.Matchups;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.dto.GamePlanDTO;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GamePlanDTOMapperTest {

    @Test
    void toDTO_mapsTeamsToTeamDto() throws Exception {
        GamePlanDTOMapperImpl mapper = new GamePlanDTOMapperImpl();
        setField(mapper, "teamDTOMapper", new TeamDTOMapperImpl());
        setField(mapper, "inGamePlayerDTOMapper", mock(InGamePlayerDTOMapper.class));
        setField(mapper, "matchupsDtoMapper", mock(MatchupsDtoMapper.class));

        Team ownerTeam = new Team(UUID.randomUUID(), AgeCategory.U18, Gender.MALE, "home");
        ownerTeam.setClubID(UUID.randomUUID());
        ownerTeam.setPlayers(List.of());

        Team opponentTeam = new Team(UUID.randomUUID(), AgeCategory.U18, Gender.FEMALE, "away");
        opponentTeam.setClubID(UUID.randomUUID());
        opponentTeam.setPlayers(List.of());

        GamePlan gamePlan = new GamePlan(UUID.randomUUID(), ownerTeam, opponentTeam);
        gamePlan.setActivePlayers(List.of());
        gamePlan.setMatchups(Matchups.empty());
        gamePlan.setPositions(Map.of());
        gamePlan.setDefenseType(DefenseType.ZONE_2_3);

        GamePlanDTO dto = mapper.toDTO(gamePlan);

        assertThat(dto.ownerTeam()).isNotNull();
        assertThat(dto.ownerTeam().id()).isEqualTo(ownerTeam.getId());
        assertThat(dto.ownerTeam().name()).isEqualTo(ownerTeam.getName());
        assertThat(dto.ownerTeam().category()).isEqualTo(ownerTeam.getCategory());
        assertThat(dto.ownerTeam().gender()).isEqualTo(ownerTeam.getGender());
        assertThat(dto.ownerTeam().clubId()).isEqualTo(ownerTeam.getClubID());

        assertThat(dto.opponentTeam()).isNotNull();
        assertThat(dto.opponentTeam().id()).isEqualTo(opponentTeam.getId());
        assertThat(dto.opponentTeam().name()).isEqualTo(opponentTeam.getName());
        assertThat(dto.opponentTeam().category()).isEqualTo(opponentTeam.getCategory());
        assertThat(dto.opponentTeam().gender()).isEqualTo(opponentTeam.getGender());
        assertThat(dto.opponentTeam().clubId()).isEqualTo(opponentTeam.getClubID());
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
