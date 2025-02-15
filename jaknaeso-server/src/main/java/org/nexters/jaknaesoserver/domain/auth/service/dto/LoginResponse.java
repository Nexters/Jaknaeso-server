package org.nexters.jaknaesoserver.domain.auth.service.dto;

public record LoginResponse(
    Long memberId, boolean isCompletedOnboarding, TokenResponse tokenInfo) {}
