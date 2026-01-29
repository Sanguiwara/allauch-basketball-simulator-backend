package com.sanguiwara.baserecords;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class Club {

    private List<Team> teams = new ArrayList<>();
    private UUID id;
    private final String name;
}
