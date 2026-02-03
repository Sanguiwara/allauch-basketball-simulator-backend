package com.sanguiwara.factory;

import com.sanguiwara.baserecords.AgeCategory;
import com.sanguiwara.baserecords.Gender;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.Team;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TeamFactory {


    public Team generateTeam(AgeCategory ageCategory, Gender gender, List<Player> players, String name) {
        var team = new Team(null, ageCategory, gender, name + " " + ageCategory + " " + gender);
        team.setPlayers(players);
        return team;
    }

}
