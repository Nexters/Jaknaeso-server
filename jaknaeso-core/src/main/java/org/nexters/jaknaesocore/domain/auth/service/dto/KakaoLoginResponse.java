package org.nexters.jaknaesocore.domain.auth.service.dto;

public record KakaoLoginResponse(Long memberId, String accessToken, String refreshToken) {}
