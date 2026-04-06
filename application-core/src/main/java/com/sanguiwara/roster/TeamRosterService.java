package com.sanguiwara.roster;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.factory.PlayerArchetype;
import com.sanguiwara.factory.PlayerGenerator;
import com.sanguiwara.repository.PlayerRepository;
import com.sanguiwara.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamRosterService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final PlayerGenerator playerGenerator;

    public Player createPlayerForTeam(UUID teamId, PlayerArchetype archetype) {
        Objects.requireNonNull(teamId, "teamId");
        Objects.requireNonNull(archetype, "archetype");

        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team not found"));

        Player p = playerGenerator.generatePlayer(archetype);

        // Attach to team (join table team_players).
        HashSet<UUID> teamIds = new HashSet<>();
        teamIds.add(teamId);
        p.setTeamsID(teamIds);

        // Align club if the team is owned by a club.
        p.setClubID(team.getClubID());

        return playerRepository.save(p);
    }
}

