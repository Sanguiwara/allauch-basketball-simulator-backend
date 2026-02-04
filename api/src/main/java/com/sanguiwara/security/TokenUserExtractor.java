package com.sanguiwara.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

@Component
public class TokenUserExtractor {

    public TokenUserInfo fromAuthentication(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return fromJwt(jwtAuth.getToken());
        }

        if (authentication instanceof OAuth2AuthenticationToken oauth2Auth) {
            return fromAttributes(oauth2Auth.getPrincipal().getAttributes());
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return fromJwt(jwt);
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unsupported authentication type");
    }

    private TokenUserInfo fromJwt(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        String sub = asString(claims.get("sub"));
        if (sub == null || sub.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing 'sub' claim");
        }

        String issuer = jwt.getIssuer() != null ? jwt.getIssuer().toString() : asString(claims.get("iss"));
        String audience = stringifyAudience(jwt.getAudience());
        Instant expiresAt = jwt.getExpiresAt() != null ? jwt.getExpiresAt() : asInstant(claims.get("exp"));

        return new TokenUserInfo(
                sub,
                asString(claims.get("email")),
                asString(claims.get("given_name")),
                asString(claims.get("family_name")),
                asString(claims.get("name")),
                firstNonBlank(asString(claims.get("preferred_username")), asString(claims.get("nickname"))),
                asString(claims.get("picture")),
                issuer,
                audience,
                expiresAt
        );
    }

    private TokenUserInfo fromAttributes(Map<String, Object> attributes) {
        String sub = asString(attributes.get("sub"));
        if (sub == null || sub.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing 'sub' attribute");
        }

        String issuer = asString(attributes.get("iss"));
        String audience = stringifyAudience(attributes.get("aud"));
        Instant expiresAt = asInstant(attributes.get("exp"));

        return new TokenUserInfo(
                sub,
                asString(attributes.get("email")),
                asString(attributes.get("given_name")),
                asString(attributes.get("family_name")),
                asString(attributes.get("name")),
                firstNonBlank(asString(attributes.get("preferred_username")), asString(attributes.get("nickname"))),
                asString(attributes.get("picture")),
                issuer,
                audience,
                expiresAt
        );
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }

    private static String stringifyAudience(Object audValue) {
        switch (audValue) {
            case null -> {
                return null;
            }
            case String s -> {
                return s;
            }
            case Collection<?> collection -> {
                StringJoiner joiner = new StringJoiner(",");
                for (Object item : collection) {
                    if (item != null) {
                        joiner.add(Objects.toString(item));
                    }
                }
                return joiner.toString();
            }
            default -> {
            }
        }
        return Objects.toString(audValue);
    }

    private static String stringifyAudience(Collection<String> aud) {
        if (aud == null || aud.isEmpty()) {
            return null;
        }
        StringJoiner joiner = new StringJoiner(",");
        for (String item : aud) {
            if (item != null && !item.isBlank()) {
                joiner.add(item);
            }
        }
        String result = joiner.toString();
        return result.isBlank() ? null : result;
    }

    private static String asString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String s) {
            return s;
        }
        return Objects.toString(value);
    }

    private static Instant asInstant(Object value) {
        switch (value) {
            case null -> {
                return null;
            }
            case Instant instant -> {
                return instant;
            }
            case Number number -> {
                return Instant.ofEpochSecond(number.longValue());
            }
            case String s -> {
                try {
                    return Instant.ofEpochSecond(Long.parseLong(s));
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
            default -> {
            }
        }
        return null;
    }
}

