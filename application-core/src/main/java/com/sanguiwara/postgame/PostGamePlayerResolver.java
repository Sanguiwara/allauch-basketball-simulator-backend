package com.sanguiwara.postgame;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public final class PostGamePlayerResolver {

    private PostGamePlayerResolver() {
    }

    public static List<Player> resolveAffectedPlayers(Game game) {
        Map<UUID, Player> playersById = new LinkedHashMap<>();

        rosterPlayers(game)
                .forEach(player -> playersById.put(player.getId(), player));

        // Active player instances carry post-game mutations, so keep them when present.
        activePlayers(game)
                .map(InGamePlayer::getPlayer)
                .forEach(player -> playersById.put(player.getId(), player));

        return List.copyOf(playersById.values());
    }

    private static Stream<Player> rosterPlayers(Game game) {
        return Stream.concat(
                game.getHomeGamePlan().getOwnerTeam().getPlayers().stream(),
                game.getAwayGamePlan().getOwnerTeam().getPlayers().stream());
    }

    private static Stream<InGamePlayer> activePlayers(Game game) {
        return Stream.concat(
                game.getHomeGamePlan().getActivePlayers().stream(),
                game.getAwayGamePlan().getActivePlayers().stream()
        );
    }
}
