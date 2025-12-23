package baserecords;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class Team {


    private final List<Player> players;
    private final List<Player> activePlayers;




}
