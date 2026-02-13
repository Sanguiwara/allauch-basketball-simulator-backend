package com.sanguiwara.initializer;

import com.sanguiwara.baserecords.*;
import com.sanguiwara.executor.GameExecutor;
import com.sanguiwara.factory.ClubNameFactory;
import com.sanguiwara.factory.GamePlanFactory;
import com.sanguiwara.factory.PlayerFactory;
import com.sanguiwara.factory.TeamFactory;
import com.sanguiwara.repository.*;
import com.sanguiwara.timeevent.EventManager;
import com.sanguiwara.timeevent.GameTimeEvent;
import com.sanguiwara.timeevent.TimeEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private final ClubRepository clubRepository;
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
            String randomFrenchBasketClubName = ClubNameFactory.generateRandomFrenchBasketClubName();
            Club club = new Club(randomFrenchBasketClubName);
            club = clubRepository.save(club);

            //GENERATE TEAMS FOR EACH AGE CATEGORY

            Team team = teamFactory.generateTeam(AgeCategory.SENIOR, Gender.MALE, new ArrayList<>(), randomFrenchBasketClubName);
            team.setClubID(club.getId());
            team = teamRepository.save(team);

            List<Player> players = new ArrayList<>();
            for (int k = 0; k < NB_PLAYERS_PER_TEAM; k++) {
                Player player = playerFactory.generatePlayer("");
                player.setClubID(club.getId());

                player = playerRepository.save(player);
                players.add(player);


            }
            team.setPlayers(players);
            club.getTeams().add(team);
            team = teamRepository.save(team);
            clubRepository.save(club);


            TeamSeason teamSeason = new TeamSeason(null, team, leagueSeason.getId(), seasonYear);
            teamSeason = teamForSeasonRepository.save(teamSeason);
            teamSeasonList.add(teamSeason);


        }
        leagueSeason.getTeamSeasons().addAll(teamSeasonList);
        leagueSeason = leagueSeasonRepository.save(leagueSeason);

        createGamesForSeason(startDate, leagueSeason);

        eventManager.listAllOrdered().forEach(TimeEvent::execute);

    }


    private void createGamesForSeason(Instant startDate, LeagueSeason leagueSeason) {
        List<TeamSeason> teams = leagueSeason.getTeamSeasons();
        int n = teams.size();

        if (n < 2) return;

        if (n % 2 != 0) {
            throw new IllegalArgumentException(
                    "Nombre d'équipes impair: impossible que toutes les équipes jouent tous les jours."
            );
        }

        // Méthode du cercle (round-robin)
        TeamSeason fixed = teams.getFirst();
        List<TeamSeason> rotating = new ArrayList<>(teams.subList(1, n));

        Instant day = startDate;
        int rounds = n - 1;

        // ALLER
        for (int r = 0; r < rounds; r++) {
            createRoundGames(fixed, rotating, leagueSeason, day, false);
            day = day.plus(1, ChronoUnit.DAYS);
            rotate(rotating);
        }

        // RETOUR (home/away inversés) — on repart de la rotation initiale
        rotating = new ArrayList<>(teams.subList(1, n));
        for (int r = 0; r < rounds; r++) {
            createRoundGames(fixed, rotating, leagueSeason, day, true);
            day = day.plus(1, ChronoUnit.DAYS);
            rotate(rotating);
        }
    }

    /**
     * Crée les matchs d'une "journée" :
     * - n/2 matchs
     * - chaque équipe apparaît exactement une fois
     * - reverse=true => home/away inversés pour le retour
     * On CONSERVE ton comportement GamePlan.save() puis update() après setActivePlayers.
     */
    private void createRoundGames(
            TeamSeason fixed,
            List<TeamSeason> rotating,
            LeagueSeason leagueSeason,
            Instant day,
            boolean reverse
    ) {
        int n = rotating.size() + 1;
        int half = n / 2;

        List<TeamSeason> roundTeams = new ArrayList<>(n);
        roundTeams.add(fixed);
        roundTeams.addAll(rotating);

        for (int i = 0; i < half; i++) {
            TeamSeason t1 = roundTeams.get(i);
            TeamSeason t2 = roundTeams.get(n - 1 - i);

            TeamSeason homeTeam = reverse ? t2 : t1;
            TeamSeason visitorTeam = reverse ? t1 : t2;

            Result result = getResult(homeTeam, visitorTeam);
            // =========================

            Game game = new Game(null, result.homeGamePlan(), result.visitorGamePlan(), leagueSeason, day);
            game = gameRepository.save(game);

            GameTimeEvent gameTimeEvent = new GameTimeEvent(null, day, game.getId(), gameExecutor);
            gameTimeEvent = gameTimeEventRepository.save(gameTimeEvent);

            eventManager.schedule(gameTimeEvent);
        }
    }

    private @NonNull Result getResult(TeamSeason homeTeam, TeamSeason visitorTeam) {
        // === Ton flow inchangé ===
        GamePlan homeGamePlan = gamePlanFactory.generateGamePlan(homeTeam.team(), visitorTeam.team());
        GamePlan visitorGamePlan = gamePlanFactory.generateGamePlan(visitorTeam.team(), homeTeam.team());

        homeGamePlan = gamePlanRepository.save(homeGamePlan);
        visitorGamePlan = gamePlanRepository.save(visitorGamePlan);


        GamePlan finalHomeGamePlan = homeGamePlan;
        List<InGamePlayer> activePlayers = homeGamePlan.getOwnerTeam().getPlayers().stream()
                .limit(10)
                .map(player -> new InGamePlayer(player, finalHomeGamePlan.getId())) // map Player -> InGamePlayer
                .toList();
        homeGamePlan.setActivePlayers(activePlayers);
        homeGamePlan = gamePlanRepository.update(homeGamePlan);

        GamePlan finalVisitorGamePlan = visitorGamePlan;
        List<InGamePlayer> visitorActivePlayers = visitorGamePlan.getOwnerTeam().getPlayers().stream()
                .limit(10)
                .map(player -> new InGamePlayer(player, finalVisitorGamePlan.getId()))
                .toList();
        visitorGamePlan.setActivePlayers(visitorActivePlayers);
        visitorGamePlan = gamePlanRepository.update(visitorGamePlan);
        return new Result(homeGamePlan, visitorGamePlan);
    }

    private record Result(GamePlan homeGamePlan, GamePlan visitorGamePlan) {
    }

    /**
     * Rotation "circle method"
     * rotating=[a,b,c,d,e] => [e,a,b,c,d]
     */
    private void rotate(List<TeamSeason> rotating) {
        if (rotating.isEmpty()) return;
        TeamSeason last = rotating.removeLast();
        rotating.addFirst(last);
    }


}

