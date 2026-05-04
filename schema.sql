CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 4) Enums
-- Note: enums are stored as VARCHAR for Hibernate compatibility (schema-first, no runtime DDL drift).
-- If you want native Postgres enums later, reintroduce CREATE TYPE + column types and keep ddl-auto=validate.

-- 5) Tables
CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,
                          sub VARCHAR(255) NOT NULL UNIQUE,
                          email VARCHAR(255),
                          name VARCHAR(255)
);

CREATE TABLE clubs (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name VARCHAR(255) NOT NULL,
                       user_id BIGINT UNIQUE,
                       CONSTRAINT fk_clubs_user
                           FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE teams (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name VARCHAR(255),
                       age_category VARCHAR(255) NOT NULL,
                       gender VARCHAR(255) NOT NULL,
                       club_id UUID,
                       CONSTRAINT fk_teams_club
                           FOREIGN KEY (club_id) REFERENCES clubs (id)
);

CREATE TABLE players (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name VARCHAR(255) NOT NULL,
                          team_id UUID,
                          club_id UUID,
                         birth_date INTEGER NOT NULL,
                         injured BOOLEAN NOT NULL DEFAULT FALSE,
                         tir_3_pts INTEGER NOT NULL,
                         tir_2_pts INTEGER NOT NULL,
                         lancer_franc INTEGER NOT NULL,
                         floater INTEGER NOT NULL,
                         finition_au_cercle INTEGER NOT NULL,
                         speed INTEGER NOT NULL,
                         ballhandling INTEGER NOT NULL,
                         size INTEGER NOT NULL,
                         weight INTEGER NOT NULL,
                         agressivite INTEGER NOT NULL,
                         def_exterieur INTEGER NOT NULL,
                         def_poste INTEGER NOT NULL,
                         protection_cercle INTEGER NOT NULL,
                         timing_rebond INTEGER NOT NULL,
                         agressivite_rebond INTEGER NOT NULL,
                         steal INTEGER NOT NULL,
                         timing_block INTEGER NOT NULL,
                         physique INTEGER NOT NULL,
                         basketball_iq_off INTEGER NOT NULL,
                         basketball_iq_def INTEGER NOT NULL,
                         passing_skills INTEGER NOT NULL,
                         iq INTEGER NOT NULL,
                         endurance INTEGER NOT NULL,
                         solidite INTEGER NOT NULL,
                         potentiel_skill INTEGER NOT NULL,
                         potentiel_physique INTEGER NOT NULL,
                         coachability INTEGER NOT NULL,
                         ego INTEGER NOT NULL,
                         soft_skills INTEGER NOT NULL,
                         leadership INTEGER NOT NULL,
                         morale INTEGER NOT NULL DEFAULT 50,
                         CONSTRAINT fk_players_team
                             FOREIGN KEY (team_id) REFERENCES teams (id),
                          CONSTRAINT fk_players_club
                              FOREIGN KEY (club_id) REFERENCES clubs (id)
 );

CREATE TABLE badges (
                        id BIGINT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        drop_rate DOUBLE PRECISION NOT NULL DEFAULT 0
);

-- Player badges (present/absent, unique per player)
CREATE TABLE player_badges (
                               player_id UUID NOT NULL,
                               badge_id BIGINT NOT NULL,
                               CONSTRAINT pk_player_badges PRIMARY KEY (player_id, badge_id),
                               CONSTRAINT fk_player_badges_player
                                   FOREIGN KEY (player_id) REFERENCES players (id) ON DELETE CASCADE,
                                CONSTRAINT fk_player_badges_badge
                                    FOREIGN KEY (badge_id) REFERENCES badges (id)
);
CREATE INDEX idx_player_badges_badge_id ON player_badges(badge_id);

CREATE TABLE leagues (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         age_category VARCHAR(255) NOT NULL,
                         gender VARCHAR(255) NOT NULL
);

CREATE TABLE league_seasons (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                year INTEGER NOT NULL,
                                league_id UUID NOT NULL,
                                CONSTRAINT fk_league_seasons_league
                                    FOREIGN KEY (league_id) REFERENCES leagues (id)
);

CREATE TABLE team_season (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             team_id UUID NOT NULL,
                             league_season_id UUID NOT NULL,
                             season INTEGER NOT NULL,
                             CONSTRAINT fk_team_season_team
                                 FOREIGN KEY (team_id) REFERENCES teams (id),
                             CONSTRAINT fk_team_season_league_season
                                 FOREIGN KEY (league_season_id) REFERENCES league_seasons (id)
);

CREATE TABLE gameplans (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           team_home_id UUID NOT NULL,
                           team_visitor_id UUID NOT NULL,
                           defense_type VARCHAR(255) NOT NULL DEFAULT 'MAN_TO_MAN',
                           three_pt_attempt_share DOUBLE PRECISION NOT NULL,
                           mid_range_attempt_share DOUBLE PRECISION NOT NULL,
                           drive_attempt_share DOUBLE PRECISION NOT NULL,
                           total_shot_number INTEGER NOT NULL DEFAULT 75,
                           block_score DOUBLE PRECISION NOT NULL,
                           block_probability DOUBLE PRECISION NOT NULL,
                           assist_probability DOUBLE PRECISION NOT NULL,
                           CONSTRAINT fk_gameplans_owner_team
                               FOREIGN KEY (team_home_id) REFERENCES teams (id),
                           CONSTRAINT fk_gameplans_opponent_team
                               FOREIGN KEY (team_visitor_id) REFERENCES teams (id)
);

CREATE TABLE in_game_players (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 gameplan_id UUID NOT NULL,
                                 player_id UUID NOT NULL,
                                 playmaking_contrib DOUBLE PRECISION NOT NULL,
                                 assist_weight DOUBLE PRECISION NOT NULL,
                                 rebound_contrib DOUBLE PRECISION NOT NULL,
                                 rebound_weight DOUBLE PRECISION NOT NULL,
                                 three_pt_contrib DOUBLE PRECISION NOT NULL,
                                 three_pt_weight DOUBLE PRECISION NOT NULL,
                                 two_pt_contrib DOUBLE PRECISION NOT NULL,
                                 two_pt_weight DOUBLE PRECISION NOT NULL,
                                 drive_contrib DOUBLE PRECISION NOT NULL,
                                 drive_weight DOUBLE PRECISION NOT NULL,
                                 block_contrib DOUBLE PRECISION NOT NULL,
                                 block_weight DOUBLE PRECISION NOT NULL,
                                 steal_contrib DOUBLE PRECISION NOT NULL,
                                 steal_weight DOUBLE PRECISION NOT NULL,
                                 usage_shoot INTEGER NOT NULL,
                                 usage_drive INTEGER NOT NULL,
                                 usage_post INTEGER NOT NULL,
                                 assists INTEGER NOT NULL,
                                 points INTEGER NOT NULL,
                                 off_reb INTEGER NOT NULL,
                                 def_reb INTEGER NOT NULL,
                                 steals INTEGER NOT NULL,
                                 blocks INTEGER NOT NULL,
                                 fga INTEGER NOT NULL,
                                 fgm INTEGER NOT NULL,
                                 tpa INTEGER NOT NULL,
                                 tpm INTEGER NOT NULL,
                                 two_pa INTEGER NOT NULL,
                                 two_pm INTEGER NOT NULL,
                                 is_starter BOOLEAN NOT NULL,
                                 drive_pa INTEGER NOT NULL,
                                 drive_pm INTEGER NOT NULL,
                                 minutes_played INTEGER NOT NULL,
                                 match_rating DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 three_pt_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 three_pt_defense_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 two_pt_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 two_pt_defense_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 drive_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 drive_defense_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 man_to_man_playmaking_off_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 man_to_man_playmaking_def_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 zone_playmaking_off_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 zone_playmaking_def_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 zone23_defense_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 zone32_defense_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 zone212_defense_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 rebound_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 steal_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 CONSTRAINT fk_in_game_players_gameplan
                                     FOREIGN KEY (gameplan_id) REFERENCES gameplans (id),
                                 CONSTRAINT fk_in_game_players_player
                                     FOREIGN KEY (player_id) REFERENCES players (id)
);

-- ✅ CHANGEMENT STRICTEMENT NECESSAIRE ICI :
-- games ne référence plus home_team_id/away_team_id mais home_plan_id/away_plan_id,
-- et les deux FK sont UNIQUE pour imposer le OneToOne (un plan ne peut être utilisé que par un seul game)
CREATE TABLE games (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       home_plan_id UUID NOT NULL UNIQUE,
                       away_plan_id UUID NOT NULL UNIQUE,
                       execute_at TIMESTAMPTZ NOT NULL,
                       CONSTRAINT fk_games_home_gameplan
                           FOREIGN KEY (home_plan_id) REFERENCES gameplans (id),
                       CONSTRAINT fk_games_away_gameplan
                           FOREIGN KEY (away_plan_id) REFERENCES gameplans (id)
);

CREATE TABLE game_results (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              game_id UUID NOT NULL UNIQUE,

                              home_three_pt_attempts INTEGER NOT NULL,
                              home_three_pt_made INTEGER NOT NULL,
                              home_drive_attempts INTEGER NOT NULL,
                              home_drive_made INTEGER NOT NULL,
                              home_drive_fouls_drawn INTEGER NOT NULL,
                              home_two_pt_attempts INTEGER NOT NULL,
                              home_two_pt_made INTEGER NOT NULL,

                              away_three_pt_attempts INTEGER NOT NULL,
                              away_three_pt_made INTEGER NOT NULL,
                              away_drive_attempts INTEGER NOT NULL,
                              away_drive_made INTEGER NOT NULL,
                              away_drive_fouls_drawn INTEGER NOT NULL,
                              away_two_pt_attempts INTEGER NOT NULL,
                              away_two_pt_made INTEGER NOT NULL,

                              CONSTRAINT fk_game_results_game
                                  FOREIGN KEY (game_id) REFERENCES games (id)
);

CREATE TABLE game_time_events (
                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  execute_at TIMESTAMPTZ NOT NULL,
                                  game_id UUID NOT NULL,
                              CONSTRAINT fk_game_id_game
                                FOREIGN KEY (game_id) REFERENCES games (id)
);

CREATE TABLE trainings (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           execute_at TIMESTAMPTZ NOT NULL,
                           team_id UUID NOT NULL,
                           training_type VARCHAR(255) NOT NULL,
                           CONSTRAINT fk_trainings_team
                               FOREIGN KEY (team_id) REFERENCES teams (id),
                           CONSTRAINT uq_trainings_team_execute_at UNIQUE (team_id, execute_at)
);

CREATE TABLE training_time_events (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      execute_at TIMESTAMPTZ NOT NULL,
                                      training_id UUID NOT NULL UNIQUE,
                                      CONSTRAINT fk_training_time_events_training
                                          FOREIGN KEY (training_id) REFERENCES trainings (id)
);

-- Player progression deltas (1 row per player per event). event_type+event_id identify the triggering event.
CREATE TABLE player_progressions (
                                    player_id UUID NOT NULL,
                                    event_type VARCHAR(255) NOT NULL DEFAULT 'GAME',
                                    event_id  UUID NOT NULL,

                                    tir_3_pts INTEGER NULL,
                                    tir_2_pts INTEGER NULL,
                                    lancer_franc INTEGER NULL,
                                    floater INTEGER NULL,
                                    finition_au_cercle INTEGER NULL,
                                    speed INTEGER NULL,
                                    ballhandling INTEGER NULL,
                                    size INTEGER NULL,
                                    weight INTEGER NULL,
                                    agressivite INTEGER NULL,

                                    def_exterieur INTEGER NULL,
                                    def_poste INTEGER NULL,
                                    protection_cercle INTEGER NULL,
                                    timing_rebond INTEGER NULL,
                                    agressivite_rebond INTEGER NULL,
                                    steal INTEGER NULL,
                                    timing_block INTEGER NULL,

                                    physique INTEGER NULL,
                                    basketball_iq_off INTEGER NULL,
                                    basketball_iq_def INTEGER NULL,
                                    passing_skills INTEGER NULL,
                                    iq INTEGER NULL,
                                    endurance INTEGER NULL,
                                    solidite INTEGER NULL,

                                    potentiel_skill INTEGER NULL,
                                    potentiel_physique INTEGER NULL,

                                    coachability INTEGER NULL,
                                    ego INTEGER NULL,
                                    soft_skills INTEGER NULL,
                                    leadership INTEGER NULL,
                                    morale INTEGER NULL,

                                    CONSTRAINT pk_player_progressions PRIMARY KEY (player_id, event_type, event_id),
                                    CONSTRAINT fk_player_progressions_player FOREIGN KEY (player_id) REFERENCES players(id),
                                    CONSTRAINT ck_player_progressions_event_not_null CHECK (event_id IS NOT NULL)
);

CREATE INDEX idx_player_progressions_player_id ON player_progressions(player_id);
CREATE INDEX idx_player_progressions_event_id  ON player_progressions(event_id);
CREATE INDEX idx_player_progressions_event_type_id ON player_progressions(event_type, event_id);

-- Badges earned during a given progression event (snapshot of "badgesAdded").
CREATE TABLE player_progression_badges (
                                           player_id UUID NOT NULL,
                                           event_type VARCHAR(255) NOT NULL,
                                           event_id UUID NOT NULL,
                                           badge_id BIGINT NOT NULL,
                                           CONSTRAINT pk_player_progression_badges PRIMARY KEY (player_id, event_type, event_id, badge_id),
                                           CONSTRAINT fk_player_progression_badges_progression
                                               FOREIGN KEY (player_id, event_type, event_id)
                                                   REFERENCES player_progressions (player_id, event_type, event_id) ON DELETE CASCADE,
                                           CONSTRAINT fk_player_progression_badges_badge
                                               FOREIGN KEY (badge_id) REFERENCES badges (id)
);
CREATE INDEX idx_player_progression_badges_badge_id ON player_progression_badges(badge_id);

CREATE TABLE team_players (
                              team_id UUID NOT NULL,
                              player_id UUID NOT NULL,
                              PRIMARY KEY (team_id, player_id),
                              CONSTRAINT fk_team_players_team
                                  FOREIGN KEY (team_id) REFERENCES teams (id),
                              CONSTRAINT fk_team_players_player
                                  FOREIGN KEY (player_id) REFERENCES players (id)
);

CREATE TABLE game_matchups (
                               game_id UUID NOT NULL,
                               player_attacker_id UUID NOT NULL,
                               player_defender_id UUID NOT NULL,
                               PRIMARY KEY (game_id, player_attacker_id),
                               CONSTRAINT fk_game_matchups_game
                                   FOREIGN KEY (game_id) REFERENCES gameplans (id),
                               CONSTRAINT fk_game_matchups_attacker
                                   FOREIGN KEY (player_attacker_id) REFERENCES players (id),
                               CONSTRAINT fk_game_matchups_defender
                                   FOREIGN KEY (player_defender_id) REFERENCES players (id)
);

CREATE TABLE game_positions (
                                game_id UUID NOT NULL,
                                position_code VARCHAR(255) NOT NULL,
                                in_game_player_id UUID NOT NULL,
                                PRIMARY KEY (game_id, position_code),
                                CONSTRAINT fk_game_positions_game
                                    FOREIGN KEY (game_id) REFERENCES gameplans (id),
                                CONSTRAINT fk_game_positions_in_game_player
                                    FOREIGN KEY (in_game_player_id) REFERENCES in_game_players (id)
);
