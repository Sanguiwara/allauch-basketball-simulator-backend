package com.sanguiwara.repository;

import com.sanguiwara.baserecords.User;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    @NonNull
    Optional<User> findById(Long id);

    @NonNull
    Optional<User> findBySub(String sub);

    List<User> findAll();

    User save(User user);

    void deleteById(Long id);

    void deleteAll();
}

