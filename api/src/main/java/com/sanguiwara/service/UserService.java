package com.sanguiwara.service;

import com.sanguiwara.dto.ClubDTO;
import org.springframework.security.core.Authentication;

public interface UserService {
    ClubDTO associate(Authentication authentication);
}
