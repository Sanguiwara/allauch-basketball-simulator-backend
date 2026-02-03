package com.sanguiwara.service;

import com.sanguiwara.baserecords.Team;

import java.util.UUID;

public interface TeamService {
    Team getTeam(UUID uuid);

    Team save(Team team);

}
