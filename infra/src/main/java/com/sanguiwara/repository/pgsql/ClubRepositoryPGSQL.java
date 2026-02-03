package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.Club;
import com.sanguiwara.mapper.ClubMapper;
import com.sanguiwara.repository.ClubRepository;
import com.sanguiwara.repository.jpa.ClubJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ClubRepositoryPGSQL implements ClubRepository {

    private final ClubJpaRepository clubJpaRepository;
    private final ClubMapper clubMapper;

    @Override
    public @NonNull Optional<Club> findById(UUID id) {
        return clubJpaRepository.findById(id).map(clubMapper::toDomain);
    }

    @Override
    public List<Club> findAll() {
        return clubJpaRepository.findAll().stream()
                .map(clubMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Club save(Club club) {
        var entity = clubMapper.toEntity(club);
        var saved = clubJpaRepository.save(entity);
        return clubMapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        clubJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        clubJpaRepository.deleteAll();
    }
}