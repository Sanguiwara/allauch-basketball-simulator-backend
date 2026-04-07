package com.sanguiwara.controller;

import com.sanguiwara.dto.GameDTO;
import com.sanguiwara.dto.ScheduleGameRequest;
import com.sanguiwara.dto.SimplifiedGameDTO;
import com.sanguiwara.game.GameSchedulingService;
import com.sanguiwara.mapper.GameDTOMapper;
import com.sanguiwara.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
@Controller
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final GameDTOMapper gameDTOMapper;
    private final GameSchedulingService gameSchedulingService;

    @GetMapping("/teamID/{teamId}")
    public ResponseEntity<List<GameDTO>> getGameForATeam(@PathVariable UUID teamId) {

        return ResponseEntity.of(Optional.of(gameService.getAllGamesForATeam(teamId).stream().map(gameDTOMapper::toDto).toList()));

    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGame(@PathVariable UUID id) {
        return ResponseEntity.of(Optional.of(gameDTOMapper.toDto(gameService.getGameById(id))));
    }

    @GetMapping()
    public ResponseEntity<List<SimplifiedGameDTO>> getAllGames() {

        return ResponseEntity.of(Optional.of(
                gameService.getAllGameSummaries().stream()
                        .map(gameDTOMapper::toSimplifiedDto)
                        .toList()
        ));

    }

    /**
     * Schedule a single game between 2 teams at a specific instant.
     *
     * Creates GamePlans + Game + GameTimeEvent (same building blocks as SeasonInitializer round creation).
     */
    @PostMapping("/schedule")
    public ResponseEntity<UUID> scheduleGame(@RequestBody ScheduleGameRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }
        if (request.homeTeamId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "homeTeamId is required");
        }
        if (request.awayTeamId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "awayTeamId is required");
        }
        if (request.localDateTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "localDateTime is required (ISO-8601 local date-time)");
        }
        if (request.zoneId() == null || request.zoneId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "zoneId is required (e.g. Europe/Paris, America/Chicago)");
        }
        if (request.leagueSeasonId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "leagueSeasonId is required");
        }

        Instant executeAt = toInstant(request.localDateTime(), request.zoneId());
        var game = gameSchedulingService.scheduleGame(
                request.homeTeamId(),
                request.awayTeamId(),
                executeAt,
                request.leagueSeasonId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(game.getId());
    }

    /**
     * URL variant: {@code POST /games/schedule/{homeTeamId}/{awayTeamId}?localDateTime=...&zoneId=...&leagueSeasonId=...}
     */
    @PostMapping("/schedule/{homeTeamId}/{awayTeamId}")
    public ResponseEntity<UUID> scheduleGame(
            @PathVariable UUID homeTeamId,
            @PathVariable UUID awayTeamId,
            @RequestParam("localDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime localDateTime,
            @RequestParam("zoneId") String zoneId,
            @RequestParam("leagueSeasonId") UUID leagueSeasonId
    ) {
        if (localDateTime == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "localDateTime is required (ISO-8601 local date-time)");
        }
        if (zoneId == null || zoneId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "zoneId is required (e.g. Europe/Paris, America/Chicago)");
        }

        Instant executeAt = toInstant(localDateTime, zoneId);
        var game = gameSchedulingService.scheduleGame(homeTeamId, awayTeamId, executeAt, leagueSeasonId);
        return ResponseEntity.status(HttpStatus.CREATED).body(game.getId());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleConflict(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleUnreadableBody(HttpMessageNotReadableException e) {
        // Typically thrown by Jackson when the request body can't be deserialized
        // (bad UUID, bad LocalDateTime format, missing commas, etc.).
        e.getMostSpecificCause();
        String message = e.getMostSpecificCause().getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    private static Instant toInstant(LocalDateTime localDateTime, String zoneId) {
        try {
            ZoneId zone = ZoneId.of(zoneId);
            return ZonedDateTime.of(localDateTime, zone).toInstant();
        } catch (DateTimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date/time or zoneId", e);
        }
    }

}
