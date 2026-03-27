package com.sanguiwara.initializer;

import com.sanguiwara.baserecords.*;
import com.sanguiwara.executor.GameExecutor;
import com.sanguiwara.executor.TrainingExecutor;
import com.sanguiwara.factory.ClubNameFactory;
import com.sanguiwara.factory.PlayerArchetype;
import com.sanguiwara.factory.PlayerGenerator;
import com.sanguiwara.factory.TeamFactory;
import com.sanguiwara.repository.*;
import com.sanguiwara.service.GamePlanService;
import com.sanguiwara.service.PlayerService;
import com.sanguiwara.timeevent.EventManager;
import com.sanguiwara.timeevent.GameTimeEvent;
import com.sanguiwara.timeevent.TimeEvent;
import com.sanguiwara.timeevent.TrainingTimeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class SeasonInitializer {

    private final static int NB_CLUBS = 8;
    private final static int NB_PLAYERS_PER_TEAM = 12;
    private final static int NB_TEAMS_PER_CATEGORY = 1;
    private final static int NB_CATEGORIES = 1; //for now only senior
    //TODO Supprimer tous les repository et utiliser des services à la place
    private final PlayerGenerator playerGenerator;
    private final TeamFactory teamFactory;
    private final GamePlanService gamePlanService;
    private final TeamRepository teamRepository;
    private final PlayerService playerService;
    private final TeamSeasonRepository teamForSeasonRepository;
    private final LeagueSeasonRepository leagueSeasonRepository;
    private final LeagueRepository leagueRepository;
    private final GameRepository gameRepository;
    private final ClubRepository clubRepository;
    private final GameExecutor gameExecutor;
    private final TrainingExecutor trainingExecutor;
    private final EventManager eventManager;
    private final GameTimeEventRepository gameTimeEventRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingTimeEventRepository trainingTimeEventRepository;

    // Scheduling cadence: for fast iterations, we want a match and a training every 10 minutes.
    private static final long ROUND_INTERVAL_MINUTES = 10;
    private static final long TRAINING_OFFSET_MINUTES = 0;
    private static final long GAME_OFFSET_MINUTES = 2;

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


            while (players.size() < NB_PLAYERS_PER_TEAM) {
                PlayerArchetype archetype = playerGenerator.randomArchetype();
                Player player = playerGenerator.generatePlayer(archetype);
                player.setClubID(club.getId());
                players.add(playerService.savePlayer(player));
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


    void createGamesForSeason(Instant startDate, LeagueSeason leagueSeason) {
        List<TeamSeason> teams = leagueSeason.getTeamSeasons();
        int n = teams.size();

        if (n < 2) return;

        if (n % 2 != 0) {
            throw new IllegalArgumentException(
                    "Nombre d'équipes impair: impossible que toutes les équipes jouent à chaque round."
            );
        }

        // Méthode du cercle (round-robin)
        TeamSeason fixed = teams.getFirst();
        List<TeamSeason> rotating = new ArrayList<>(teams.subList(1, n));

        Instant roundStart = startDate;
        int rounds = n - 1;

        // ALLER
        for (int r = 0; r < rounds; r++) {
            createRoundGames(fixed, rotating, leagueSeason, roundStart, false);
            roundStart = roundStart.plus(ROUND_INTERVAL_MINUTES, ChronoUnit.MINUTES);
            rotate(rotating);
        }

        // RETOUR (home/away inversés) — on repart de la rotation initiale
        rotating = new ArrayList<>(teams.subList(1, n));
        for (int r = 0; r < rounds; r++) {
            createRoundGames(fixed, rotating, leagueSeason, roundStart, true);
            roundStart = roundStart.plus(ROUND_INTERVAL_MINUTES, ChronoUnit.MINUTES);
            rotate(rotating);
        }
    }

    /**
     * Crée les matchs d'un "round" :
     * - n/2 matchs
     * - chaque équipe apparaît exactement une fois
     * - reverse=true => home/away inversés pour le retour
     * On CONSERVE ton comportement GamePlan.save() puis update() après setActivePlayers.
     */
    private void createRoundGames(
            TeamSeason fixed,
            List<TeamSeason> rotating,
            LeagueSeason leagueSeason,
            Instant roundStart,
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

            GamePlan homeGamePlan = gamePlanService.generateGamePlan(homeTeam.team(), visitorTeam.team());
            GamePlan visitorGamePlan = gamePlanService.generateGamePlan(visitorTeam.team(), homeTeam.team());
            // =========================

            Instant trainingExecuteAt = roundStart.plus(TRAINING_OFFSET_MINUTES, ChronoUnit.MINUTES);
            Instant gameExecuteAt = roundStart.plus(GAME_OFFSET_MINUTES, ChronoUnit.MINUTES);

            createAndScheduleTraining(homeTeam.team(), trainingExecuteAt);
            createAndScheduleTraining(visitorTeam.team(), trainingExecuteAt);

            Game game = new Game(null, homeGamePlan, visitorGamePlan, leagueSeason, gameExecuteAt);
            game = gameRepository.save(game);

            GameTimeEvent gameTimeEvent = new GameTimeEvent(null, gameExecuteAt, game.getId(), gameExecutor);
            gameTimeEvent = gameTimeEventRepository.save(gameTimeEvent);

            eventManager.schedule(gameTimeEvent);


        }
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

    private void createAndScheduleTraining(Team team, Instant executeAt) {

        //TODO Utiliser TrainingService.createTraining
        Objects.requireNonNull(team, "team");

        TrainingType[] trainingTypes = TrainingType.values();
        TrainingType trainingType = trainingTypes[ThreadLocalRandom.current().nextInt(trainingTypes.length)];
        Training training = new Training(null, executeAt, team, trainingType);
        Training saved = trainingRepository.save(training);

        // Event id is generated by persistence; trainingId links the event to the training.
        TrainingTimeEvent timeEvent = new TrainingTimeEvent(null, saved.getExecuteAt(), saved.getId(), trainingExecutor);
        TrainingTimeEvent persisted = trainingTimeEventRepository.save(timeEvent);
        eventManager.schedule(persisted);
    }


}

