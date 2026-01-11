package com.sanguiwara.baserecords;

import java.util.List;

public record Club(List<Team> teams,Long id, String name) {
}
