package com.sanguiwara.baserecords;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class LeagueSeason {

    private League league;
    private List<TeamForSeason> teams;
    private final int year;
}
