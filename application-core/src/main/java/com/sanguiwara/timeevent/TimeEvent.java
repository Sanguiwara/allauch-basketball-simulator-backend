package com.sanguiwara.timeevent;

import java.time.Instant;
import java.util.UUID;

public interface TimeEvent {
    UUID getId();
    Instant getExecuteAt();
    void execute();


}
