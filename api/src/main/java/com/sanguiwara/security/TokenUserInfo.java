package com.sanguiwara.security;

import java.time.Instant;

public record TokenUserInfo(
        String sub,
        String email,
        String givenName,
        String familyName,
        String name,
        String preferredUsername,
        String picture,
        String issuer,
        String audience,
        Instant expiresAt
) {
}

