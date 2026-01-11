package com.sanguiwara.baserecords;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class GamePlan {


    private final UUID id;

    private final Team teamHome;
    private final Team teamVisitor;

    private  List<InGamePlayer> activePlayers;

    private  Map<Player, Player> matchups;
    private  Map<Position, InGamePlayer> positions;

}
