package com.sanguiwara.service;

import com.sanguiwara.baserecords.Team;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    Team getTeam(UUID uuid);

    List<Team> getAllTeams();

    Team save(Team team);

    Team updateName(UUID id, String name);

}
