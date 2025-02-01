package org.nexters.jaknaesocore.domain.auth.restclient.dto;

public record KakaoTokenResponse(
    String tokenType,
    String accessToken,
    Integer expiresIn,
    String refreshToken,
    Integer refreshTokenExpiresIn) {}
