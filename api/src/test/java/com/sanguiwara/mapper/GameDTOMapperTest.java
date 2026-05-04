package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.AgeCategory;
import com.sanguiwara.baserecords.DefenseType;
import com.sanguiwara.baserecords.Game;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.Gender;
import com.sanguiwara.baserecords.MatchupAttacker;
import com.sanguiwara.baserecords.MatchupDefender;
import com.sanguiwara.baserecords.Matchups;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.dto.GameDTO;
import com.sanguiwara.service.PlayerService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameDTOMapperTest {

    @Test
    void toDto_includesHomeAndAwayMatchupsFromGamePlans() throws Exception {
        GameDTOMapperImpl mapper = new GameDTOMapperImpl();

        // The generated mapper is a Spring component with @Autowired fields.
        // In unit tests we inject the minimal dependencies manually.
        GameResultDTOMapper gameResultDTOMapper = mock(GameResultDTOMapper.class);
        when(gameResultDTOMapper.toDto((com.sanguiwara.result.GameResult) null)).thenReturn(null);
        setField(mapper, "gameResultDTOMapper", gameResultDTOMapper);

        setField(mapper, "inGamePlayerDTOMapper", mock(InGamePlayerDTOMapper.class));
        setField(mapper, "playerProgressionDTOMapper", mock(PlayerProgressionDTOMapper.class));

        PlayerService playerService = mock(PlayerService.class);
        PlayerIdMapper playerIdMapper = new PlayerIdMapper(playerService);
        setField(mapper, "matchupsDtoMapper", new MatchupsDtoMapper(playerIdMapper));

        Team homeTeam = new Team(UUID.randomUUID(), AgeCategory.U18, Gender.MALE, "home");
        Team awayTeam = new Team(UUID.randomUUID(), AgeCategory.U18, Gender.MALE, "away");

        GamePlan homeGamePlan = new GamePlan(UUID.randomUUID(), homeTeam, awayTeam);
        GamePlan awayGamePlan = new GamePlan(UUID.randomUUID(), awayTeam, homeTeam);

        Player h1 = Player.builder().id(UUID.randomUUID()).name("h1").birthDate(2000).build();
        Player h2 = Player.builder().id(UUID.randomUUID()).name("h2").birthDate(2000).build();
        Player a1 = Player.builder().id(UUID.randomUUID()).name("a1").birthDate(2000).build();
        Player a2 = Player.builder().id(UUID.randomUUID()).name("a2").birthDate(2000).build();

        homeGamePlan.setMatchups(Matchups.of(Map.of(
                new MatchupDefender(h1), new MatchupAttacker(a1)
        )));
        awayGamePlan.setMatchups(Matchups.of(Map.of(
                new MatchupDefender(a2), new MatchupAttacker(h2)
        )));

        homeGamePlan.setDefenseType(DefenseType.ZONE_2_3);
        awayGamePlan.setDefenseType(DefenseType.MAN_TO_MAN);

        homeGamePlan.setActivePlayers(List.of());
        awayGamePlan.setActivePlayers(List.of());

        Game game = new Game(UUID.randomUUID(), homeGamePlan, awayGamePlan, null, Instant.now());

        GameDTO dto = mapper.toDto(game);

        assertThat(dto.homeDefenseType()).isEqualTo(DefenseType.ZONE_2_3);
        assertThat(dto.awayDefenseType()).isEqualTo(DefenseType.MAN_TO_MAN);
        assertThat(dto.homeMatchups()).containsExactlyEntriesOf(Map.of(h1.getId(), a1.getId()));
        assertThat(dto.awayMatchups()).containsExactlyEntriesOf(Map.of(a2.getId(), h2.getId()));
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
