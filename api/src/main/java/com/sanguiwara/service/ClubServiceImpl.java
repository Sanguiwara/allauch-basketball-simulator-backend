package com.sanguiwara.service;

import com.sanguiwara.baserecords.Club;
import com.sanguiwara.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {

    private final ClubRepository clubRepository;

    @Override
    public Club getClub(UUID id) {
        return clubRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }
}

