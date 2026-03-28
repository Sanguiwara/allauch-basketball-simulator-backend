package com.sanguiwara.service;

import com.sanguiwara.baserecords.Club;

import java.util.List;
import java.util.UUID;

public interface ClubService {
    Club getClub(UUID id);

    List<Club> getAllClubs();

    Club updateName(UUID id, String name);
}
