package com.sanguiwara.service;

import com.sanguiwara.baserecords.Team;
import com.sanguiwara.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;


    @Override
    public Team getTeam(UUID uuid) {
        return teamRepository.findById(uuid).orElseThrow();
    }

    @Override
    public Team save(Team team) {
        return teamRepository.save(team);
    }

}
