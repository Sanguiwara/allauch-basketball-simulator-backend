package com.sanguiwara.baserecords;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgeCategory {
    U11("Moins de 11 ans"),
    U13("Moins de 13 ans"),
    U15("Moins de 15 ans"),
    U18("Moins de 18 ans"),
    U21("Moins de 21 ans"),
    SENIOR("Senior");

    private final String label;
}
