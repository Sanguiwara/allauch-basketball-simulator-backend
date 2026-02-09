package com.sanguiwara.baserecords;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;

@RequiredArgsConstructor
@Getter
@Setter
public class LeagueSeason {


    private final UUID id;
    private final League league;
    private final List<TeamSeason> teamSeasons = new ArrayList<>();
    private final int year;
}
