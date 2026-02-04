package com.sanguiwara.repository.pgsql;

import com.sanguiwara.baserecords.User;
import com.sanguiwara.entity.UserEntity;
import com.sanguiwara.mapper.UserMapper;
import com.sanguiwara.repository.UserRepository;
import com.sanguiwara.repository.jpa.UserJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryPGSQL implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public @NonNull Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public @NonNull Optional<User> findBySub(String sub) {
        return userJpaRepository.findBySub(sub).map(userMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public User save(User user) {
        UserEntity entity = user.getId() != null
                ? userJpaRepository.findById(user.getId()).orElseGet(UserEntity::new)
                : userJpaRepository.findBySub(user.getSub()).orElseGet(UserEntity::new);

        if (entity.getId() == null) {
            entity.setSub(user.getSub());
        }

        userMapper.updateEntity(entity, user);
        var saved = userJpaRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        userJpaRepository.deleteAll();
    }
}

