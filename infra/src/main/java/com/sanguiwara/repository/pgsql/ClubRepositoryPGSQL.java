package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.Club;
import com.sanguiwara.entity.ClubEntity;
import com.sanguiwara.entity.UserEntity;
import com.sanguiwara.mapper.ClubMapper;
import com.sanguiwara.repository.ClubRepository;
import com.sanguiwara.repository.jpa.ClubJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ClubRepositoryPGSQL implements ClubRepository {

    private final ClubJpaRepository clubJpaRepository;
    private final ClubMapper clubMapper;
    private final EntityManager entityManager;

    @Override
    public @NonNull Optional<Club> findById(UUID id) {
        return clubJpaRepository.findById(id).map(clubMapper::toDomain);
    }

    @Override
    public @NonNull Optional<Club> findByUserSub(@NonNull String sub) {
        return clubJpaRepository.findByUser_Sub(sub).map(clubMapper::toDomain);
    }

    @Override
    public List<UUID> findAllIdsWithoutUser() {
        return clubJpaRepository.findAllByUserIsNull().stream()
                .map(ClubEntity::getId)
                .collect(Collectors.toList());
    }
    @Override
    public List<Club> findAll() {
        return clubJpaRepository.findAll().stream()
                .map(clubMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Club attachUser(UUID clubId, Long userId) {
        var clubEntity = clubJpaRepository.findById(clubId).orElseThrow(NoSuchElementException::new);
        if (clubEntity.getUser() != null && !Objects.equals(clubEntity.getUser().getId(), userId)) {
            throw new IllegalStateException("Club already associated to another user");
        }
        UserEntity userRef = entityManager.getReference(UserEntity.class, userId);
        attachUser(clubEntity, userRef);
        var saved = clubJpaRepository.saveAndFlush(clubEntity);
        return clubMapper.toDomain(saved);
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

    private void attachUser(ClubEntity clubEntity, UserEntity newUser) {
        if (newUser == null) {
            throw new IllegalArgumentException("user is required");
        }
        if (clubEntity.getUser() != null) {
            throw new IllegalStateException("Club already associated");
        }
        if (newUser.getClub() != null) {
            throw new IllegalStateException("User already associated");
        }
        clubEntity.setUser(newUser);
        newUser.setClub(clubEntity);
    }
}
