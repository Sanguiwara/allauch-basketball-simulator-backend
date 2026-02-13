package com.sanguiwara.baserecords;


import java.util.UUID;

public record TeamSeason(UUID id, Team team, UUID leagueSeasonId, int season) {

}
