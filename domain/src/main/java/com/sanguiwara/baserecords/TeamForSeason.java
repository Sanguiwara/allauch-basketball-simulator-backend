package com.sanguiwara.baserecords;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class TeamForSeason {

    private final Team team;
    private final LeagueSeason leagueSeason;
    private final int season;

}
