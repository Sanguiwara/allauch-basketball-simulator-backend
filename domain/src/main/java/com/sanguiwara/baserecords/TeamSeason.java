package com.sanguiwara.baserecords;


import java.util.UUID;

public record TeamSeason(UUID uuid, Team team, UUID leagueSeasonId, int season) {

}
