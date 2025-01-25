package org.nexters.jaknaesoserver.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RefreshTokenValidator {

  private final JwtParser jwtParser;

  // TODO : 리프레시토큰 저장소가 존재 한다면 실제 저장소와 비교하여 유효한 토큰인지 확인합니다.

}
