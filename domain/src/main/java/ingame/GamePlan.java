package ingame;


import baserecords.Position;
import baserecords.Team;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Setter
public class GamePlan {

    private final Team teamHome;
    private final Team teamVisitor;

    private final List<InGamePlayer> activePlayers;
    //private final Map<Player, Player> matchups;
    private  final Map<Position, InGamePlayer> positions;

}
