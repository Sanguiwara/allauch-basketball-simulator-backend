package com.sanguiwara.baserecords;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class LeagueSeason {


    private final UUID id;
    private final League league;
    private final List<TeamSeason> teamSeasons = new ArrayList<>();
    private final int year;
}
