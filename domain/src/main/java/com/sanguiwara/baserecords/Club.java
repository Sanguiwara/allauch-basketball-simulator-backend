package com.sanguiwara.baserecords;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class Club {

    private List<Team> teams = new ArrayList<>();
    private UUID id;
    private final String name;
    private List<Player> players = new  ArrayList<>();
}
