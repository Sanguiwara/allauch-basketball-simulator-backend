package com.sanguiwara.gameevent;

import java.util.UUID;

public interface ShotEvent {
    UUID shooterId();          // shooter ou attacker
    int index();             // shotIndex / shotNumber / driveNumber
    boolean assisted();
    UUID assisterId();       // nullable
    double successPct();     // shotPct / successPct
    boolean made();
    double advantageMatchup();
    boolean blocked();
}