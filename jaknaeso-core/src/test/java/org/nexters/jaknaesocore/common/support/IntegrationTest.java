package org.nexters.jaknaesocore.common.support;

import org.nexters.jaknaesocore.domain.auth.restclient.KakaoAuthClient;
import org.nexters.jaknaesocore.domain.auth.restclient.KakaoClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTest {

  @MockitoBean protected KakaoClient kakaoClient;

  @MockitoBean protected KakaoAuthClient kakaoAuthClient;
}
