package com.sanguiwara.service;

import com.sanguiwara.baserecords.AgeCategory;
import com.sanguiwara.baserecords.Gender;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.factory.TeamFactory;
import com.sanguiwara.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final TeamFactory teamFactory;
    private final PlayerService playerService;


    @Override
    public Team getTeam(UUID uuid){
        return teamRepository.findById(uuid).orElseThrow();
    }

    @Override
    public Team save(Team team){
        return teamRepository.save(team);
    }

    @Override
    public Team generateTeam(AgeCategory ageCategory, Gender gender){
        List<Player> players = java.util.stream.IntStream.range(0, 10)
                .mapToObj(_ -> playerService.generatePlayer())
                .toList();
        var team = teamFactory.generateTeam(ageCategory, gender, players);
        save(team);
        return team;
    }
}
