package com.sanguiwara.service;

import com.sanguiwara.baserecords.AgeCategory;
import com.sanguiwara.baserecords.Club;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.Gender;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.repository.ClubRepository;
import com.sanguiwara.repository.GamePlanRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GamePlanServiceImplTest {

    @Test
    void getNextUpcomingGamePlanForAUserSub_returnsNextUpcomingGamePlanForUsersClub() {
        GamePlanRepository gamePlanRepository = mock(GamePlanRepository.class);
        ClubRepository clubRepository = mock(ClubRepository.class);

        GamePlanServiceImpl service = new GamePlanServiceImpl(gamePlanRepository, clubRepository);

        String sub = "auth0|user-42";
        UUID clubId = UUID.randomUUID();

        Club club = new Club("club");
        club.setId(clubId);

        Team t1 = new Team(UUID.randomUUID(), AgeCategory.U18, Gender.MALE, "team-1");
        Team t2 = new Team(UUID.randomUUID(), AgeCategory.U18, Gender.MALE, "team-2");
        GamePlan gamePlan = new GamePlan(UUID.randomUUID(), t1, t2);

        when(clubRepository.findByUserSub(eq(sub))).thenReturn(Optional.of(club));
        when(gamePlanRepository.findNextUpcomingGamePlanForClub(eq(clubId))).thenReturn(Optional.of(gamePlan));

        assertThat(service.getNextUpcomingGamePlanForAUserSub(sub)).contains(gamePlan);
    }

    @Test
    void getNextUpcomingGamePlanForAUserSub_returnsEmptyWhenNoClubForUser() {
        GamePlanRepository gamePlanRepository = mock(GamePlanRepository.class);
        ClubRepository clubRepository = mock(ClubRepository.class);

        GamePlanServiceImpl service = new GamePlanServiceImpl(gamePlanRepository, clubRepository);

        String sub = "auth0|missing-user";
        when(clubRepository.findByUserSub(eq(sub))).thenReturn(Optional.empty());

        assertThat(service.getNextUpcomingGamePlanForAUserSub(sub)).isEmpty();
    }
}

