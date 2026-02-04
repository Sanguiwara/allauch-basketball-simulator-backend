package com.sanguiwara.service;

import com.sanguiwara.baserecords.User;
import com.sanguiwara.dto.ClubDTO;
import com.sanguiwara.mapper.ClubDTOMapper;
import com.sanguiwara.repository.ClubRepository;
import com.sanguiwara.repository.UserRepository;
import com.sanguiwara.security.TokenUserExtractor;
import com.sanguiwara.security.TokenUserInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final TokenUserExtractor tokenUserExtractor;
    private final ClubDTOMapper clubDTOMapper;

    @Override
    @Transactional
    public ClubDTO associate(Authentication authentication) {
        TokenUserInfo userInfo = tokenUserExtractor.fromAuthentication(authentication);
        User user = getOrCreateUser(userInfo);

        if (user.getClub() != null) {
            return clubRepository.findById(user.getClub().getId())
                    .map(clubDTOMapper::toDto)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Club not found"));
        }

        List<UUID> availableClubIds = clubRepository.findAllIdsWithoutUser();
        if (availableClubIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No available club found");
        }
        UUID clubToAssociateID = availableClubIds.getFirst();
        var clubToAssociate = clubRepository.attachUser(clubToAssociateID, user.getId());
        return clubDTOMapper.toDto(clubToAssociate);

    }

    private @NonNull User getOrCreateUser(TokenUserInfo userInfo) {
        User user = userRepository.findBySub(userInfo.sub())
                .orElseGet(() -> new User(userInfo.sub()));
        user.setEmail(nonBlankOrNull(userInfo.email(), user.getEmail()));
        user.setName(nonBlankOrNull(userInfo.name(), user.getName()));
        user = userRepository.save(user);
        return user;
    }


    private static String nonBlankOrNull(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }
}