package com.sanguiwara.baserecords;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class User {

    private Long id;
    private final String sub;
    private String email;
    private String name;
    private Club club;
}

