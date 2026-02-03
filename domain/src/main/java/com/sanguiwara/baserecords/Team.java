package com.sanguiwara.baserecords;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@RequiredArgsConstructor
public class Team {

    private final UUID id;
    private  List<Player> players;
    private final AgeCategory category;
    private final Gender gender;
    private final String name;
    private UUID clubID;




}
