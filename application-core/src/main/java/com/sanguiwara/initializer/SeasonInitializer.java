package com.sanguiwara.initializer;

import com.sanguiwara.baserecords.*;
import com.sanguiwara.executor.GameExecutor;
import com.sanguiwara.factory.GamePlanFactory;
import com.sanguiwara.factory.PlayerFactory;
import com.sanguiwara.factory.TeamFactory;
import com.sanguiwara.repository.*;
import com.sanguiwara.timeevent.EventManager;
import com.sanguiwara.timeevent.GameTimeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeasonInitializer {

    private final static int NB_CLUBS = 8;
    private final static int NB_PLAYERS_PER_TEAM = 12;
    private final static int NB_TEAMS_PER_CATEGORY = 1;
    private final static int NB_CATEGORIES = 1; //for now only senior

    private final PlayerFactory playerFactory;
    private final TeamFactory teamFactory;
    private final GamePlanFactory gamePlanFactory;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final TeamSeasonRepository teamForSeasonRepository;
    private final LeagueSeasonRepository leagueSeasonRepository;
    private final GamePlanRepository gamePlanRepository;
    private final LeagueRepository leagueRepository;
    private final GameRepository gameRepository;
    private final GameExecutor gameExecutor;
    private final EventManager eventManager;
    private final GameTimeEventRepository gameTimeEventRepository;

    public void createSeason(Instant startDate) {

        int seasonYear = 2024;
        League league = new League(null, AgeCategory.SENIOR, Gender.MALE, 1);
        league = leagueRepository.save(league);
        LeagueSeason leagueSeason = new LeagueSeason(null, league, seasonYear);
        leagueSeason = leagueSeasonRepository.save(leagueSeason);

        List<TeamSeason> teamSeasonList = new ArrayList<>();
        for (int i = 0; i < NB_CLUBS; i++) {
            Club club = new Club("Club " + (i + 1));
         //TODO Création des clubs a deplacer

            //GENERATE TEAMS FOR EACH AGE CATEGORY


            List<Player> players = new ArrayList<>();
            for (int k = 0; k < NB_PLAYERS_PER_TEAM; k++) {
                Player player = playerFactory.generatePlayer("");
                player = playerRepository.save(player);
                players.add(player);
            }
            Team team = teamFactory.generateTeam(AgeCategory.SENIOR, Gender.MALE, players);
            team = teamRepository.save(team);
            club.getTeams().add(team);
            leagueSeason = leagueSeasonRepository.save(leagueSeason);

            TeamSeason teamSeason = new TeamSeason(null,team, leagueSeason.getId(), seasonYear);
            teamSeasonList.add(teamSeason);
            teamForSeasonRepository.save(teamSeason);


        }
        leagueSeason.getTeamSeasons().addAll(teamSeasonList);
        leagueSeasonRepository.save(leagueSeason);

        for (TeamSeason homeTeam : leagueSeason.getTeamSeasons()) {
            Instant gameDate = startDate;

            for(TeamSeason visitorTeam : leagueSeason.getTeamSeasons()){
                if(homeTeam.equals(visitorTeam)){
                    continue;
                }
                GamePlan homeGamePlan = gamePlanFactory.generateGamePlan(homeTeam.team(), visitorTeam.team());
                GamePlan visitorGamePlan = gamePlanFactory.generateGamePlan(visitorTeam.team(), homeTeam.team());
                homeGamePlan = gamePlanRepository.save(homeGamePlan);
                visitorGamePlan = gamePlanRepository.save(visitorGamePlan);

                List<InGamePlayer> activePlayers = homeGamePlan.getTeamHome().getPlayers().stream()
                        .limit(10)
                        .map(InGamePlayer::new)
                        .toList();
                homeGamePlan.setActivePlayers(activePlayers);
                gamePlanRepository.update(homeGamePlan);


                List<InGamePlayer> visitorActivePlayers = visitorGamePlan.getTeamHome().getPlayers().stream()
                        .limit(10)
                        .map(InGamePlayer::new)
                        .toList();

                visitorGamePlan.setActivePlayers(visitorActivePlayers);
                gamePlanRepository.update(visitorGamePlan);


                Game game = new Game(null, homeGamePlan, visitorGamePlan, leagueSeason);
                game = gameRepository.save(game);
                gameDate = gameDate.plus(1, ChronoUnit.DAYS);
                GameTimeEvent gameTimeEvent = new GameTimeEvent(null, gameDate,game.getId(), gameExecutor);
                gameTimeEvent = gameTimeEventRepository.save(gameTimeEvent);


                eventManager.schedule(gameTimeEvent);


            }



        }

    }
}
