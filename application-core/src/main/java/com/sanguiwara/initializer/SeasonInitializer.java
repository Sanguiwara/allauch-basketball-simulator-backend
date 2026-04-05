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
import com.sanguiwara.timeevent.TrainingTimeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class SeasonInitializer {

    private static final int NB_CLUBS = 8;
    private static final int NB_PLAYERS_PER_TEAM = 12;
    private static final int NB_TEAMS_PER_CATEGORY = 1;
    private static final int NB_CATEGORIES = 1; // for now only senior

    // Scheduling cadence: for fast iterations, we want a match and a training every 10 minutes.
    private static final long ROUND_INTERVAL_MINUTES = 5;
    private static final long TRAINING_OFFSET_MINUTES = 0;
    private static final long GAME_OFFSET_MINUTES = 2;

    private static final ZoneId FRANCE_ZONE = ZoneId.of("Europe/Paris");
    private static final LocalTime DEFAULT_TRAINING_TIME_FR = LocalTime.of(10, 0);
    private static final LocalTime DEFAULT_GAME_TIME_FR = LocalTime.of(20, 0);

    // TODO Supprimer tous les repository et utiliser des services a la place
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

    /**
     * Legacy behavior: fast schedule (10 min rounds) + immediate execution (dev/testing).
     */
    public void createSeason(Instant startDate) {
        createSeasonWithScheduling(
                RoundScheduler.fixedInterval(
                        startDate,
                        Duration.ofMinutes(ROUND_INTERVAL_MINUTES),
                        Duration.ofMinutes(TRAINING_OFFSET_MINUTES),
                        Duration.ofMinutes(GAME_OFFSET_MINUTES)
                ),
                true
        );
    }

    /**
     * Creer une saison avec 1 "journee" (round) par jour:
     * - matchs a 20:00 heure de France
     * - entrainements a 10:00 heure de France
     * a partir du jour fourni par le endpoint.
     */
    public void createSeasonDailyMatchAndTrainingFromDay(LocalDate startDayFrance) {
        Objects.requireNonNull(startDayFrance, "startDayFrance");
        createSeasonWithScheduling(
                RoundScheduler.dailyAtTimes(startDayFrance, FRANCE_ZONE, DEFAULT_TRAINING_TIME_FR, DEFAULT_GAME_TIME_FR),
                false
        );
    }

    /**
     * Creer une saison avec 1 "journee" (round) toutes les 10 minutes a partir de tout de suite.
     */
    public void createSeasonEvery10MinutesFromNow(Instant now) {
        Objects.requireNonNull(now, "now");
        createSeasonWithScheduling(
                RoundScheduler.fixedInterval(
                        now,
                        Duration.ofMinutes(ROUND_INTERVAL_MINUTES),
                        Duration.ofMinutes(TRAINING_OFFSET_MINUTES),
                        Duration.ofMinutes(GAME_OFFSET_MINUTES)
                ),
                false
        );
    }

    /**
     * Creer une saison avec 1 "journee" (round) par jour a partir d'il y a un mois (heure de France),
     * puis derouler la saison comme si elle s'etait jouee dans le passe.
     *
     * Note: ici, on execute explicitement tous les events ordonnes (sans supprimer les events en DB).
     */
    public void createSeasonDailyFromMonthAgoAndReplay(Instant now) {
        Objects.requireNonNull(now, "now");
        LocalDate todayFrance = ZonedDateTime.ofInstant(now, FRANCE_ZONE).toLocalDate();
        LocalDate startDayFrance = todayFrance.minusMonths(1);

        createSeasonWithScheduling(
                RoundScheduler.dailyAtTimes(startDayFrance, FRANCE_ZONE, DEFAULT_TRAINING_TIME_FR, DEFAULT_GAME_TIME_FR),
                true
        );
    }

    private void createSeasonWithScheduling(RoundScheduler scheduler, boolean replayAllScheduledEvents) {

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

        createGamesForSeason(leagueSeason, scheduler);

        if (replayAllScheduledEvents) {
            // Dev/test mode: "replay everything now" and clean up persisted events as they execute.
            eventManager.runDueEvents(Instant.MAX);
        }
    }

    /**
     * Legacy test helper: schedules rounds every 10 minutes starting at startDate.
     */
    void createGamesForSeason(Instant startDate, LeagueSeason leagueSeason) {
        createGamesForSeason(
                leagueSeason,
                RoundScheduler.fixedInterval(
                        startDate,
                        Duration.ofMinutes(ROUND_INTERVAL_MINUTES),
                        Duration.ofMinutes(TRAINING_OFFSET_MINUTES),
                        Duration.ofMinutes(GAME_OFFSET_MINUTES)
                )
        );
    }

    private void createGamesForSeason(LeagueSeason leagueSeason, RoundScheduler scheduler) {
        List<TeamSeason> teams = leagueSeason.getTeamSeasons();
        int n = teams.size();

        if (n < 2) return;

        if (n % 2 != 0) {
            throw new IllegalArgumentException(
                    "Nombre d'equipes impair: impossible que toutes les equipes jouent a chaque round."
            );
        }

        // Methode du cercle (round-robin)
        TeamSeason fixed = teams.getFirst();
        List<TeamSeason> rotating = new ArrayList<>(teams.subList(1, n));

        int rounds = n - 1;

        // ALLER
        for (int r = 0; r < rounds; r++) {
            createRoundGames(fixed, rotating, leagueSeason, scheduler.timesForRound(r), false, true);
            rotate(rotating);
        }

        // RETOUR (home/away inverses)
        rotating = new ArrayList<>(teams.subList(1, n));
        for (int r = 0; r < rounds; r++) {
            createRoundGames(fixed, rotating, leagueSeason, scheduler.timesForRound(rounds + r), true, true);
            rotate(rotating);
        }
    }

    /**
     * Cree les matchs d'un "round" :
     * - n/2 matchs
     * - chaque equipe apparait exactement une fois
     * - reverse=true => home/away inverses pour le retour
     */
    private void createRoundGames(
            TeamSeason fixed,
            List<TeamSeason> rotating,
            LeagueSeason leagueSeason,
            RoundTimes roundTimes,
            boolean reverse,
            boolean scheduleTrainings
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

            Instant trainingExecuteAt = roundTimes.trainingExecuteAt();
            if (scheduleTrainings && trainingExecuteAt != null) {
                createAndScheduleTraining(homeTeam.team(), trainingExecuteAt);
                createAndScheduleTraining(visitorTeam.team(), trainingExecuteAt);
            }

            Instant gameExecuteAt = roundTimes.gameExecuteAt();
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
        Objects.requireNonNull(team, "team");
        Objects.requireNonNull(executeAt, "executeAt");

        TrainingType[] trainingTypes = TrainingType.values();
        TrainingType trainingType = trainingTypes[ThreadLocalRandom.current().nextInt(trainingTypes.length)];
        Training training = new Training(null, executeAt, team, trainingType);
        Training saved = trainingRepository.save(training);

        // Event id is generated by persistence; trainingId links the event to the training.
        TrainingTimeEvent timeEvent = new TrainingTimeEvent(null, saved.getExecuteAt(), saved.getId(), trainingExecutor);
        TrainingTimeEvent persisted = trainingTimeEventRepository.save(timeEvent);
        eventManager.schedule(persisted);
    }

    private record RoundTimes(Instant trainingExecuteAt, Instant gameExecuteAt) {
    }

    /**
     * Provides execution instants for each round index (0..rounds*2-1).
     */
    private interface RoundScheduler {
        RoundTimes timesForRound(int roundIndex);

        static RoundScheduler fixedInterval(Instant start, Duration roundInterval, Duration trainingOffset, Duration gameOffset) {
            Objects.requireNonNull(start, "start");
            Objects.requireNonNull(roundInterval, "roundInterval");
            Objects.requireNonNull(trainingOffset, "trainingOffset");
            Objects.requireNonNull(gameOffset, "gameOffset");

            return roundIndex -> {
                Instant base = start.plus(roundInterval.multipliedBy(roundIndex));
                return new RoundTimes(base.plus(trainingOffset), base.plus(gameOffset));
            };
        }

        static RoundScheduler dailyAtTimes(LocalDate startDay, ZoneId zone, LocalTime trainingTime, LocalTime gameTime) {
            Objects.requireNonNull(startDay, "startDay");
            Objects.requireNonNull(zone, "zone");
            Objects.requireNonNull(gameTime, "gameTime");

            return roundIndex -> {
                LocalDate day = startDay.plusDays(roundIndex);
                Instant gameAt = ZonedDateTime.of(day, gameTime, zone).toInstant();
                Instant trainingAt = trainingTime == null ? null : ZonedDateTime.of(day, trainingTime, zone).toInstant();
                return new RoundTimes(trainingAt, gameAt);
            };
        }
    }
}
