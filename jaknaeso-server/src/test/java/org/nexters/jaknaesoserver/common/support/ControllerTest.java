package org.nexters.jaknaesoserver.common.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nexters.jaknaesocore.domain.auth.service.OauthService;
import org.nexters.jaknaesocore.domain.character.service.CharacterService;
import org.nexters.jaknaesocore.domain.member.service.MemberService;
import org.nexters.jaknaesocore.domain.survey.service.SurveyService;
import org.nexters.jaknaesoserver.config.TestSecurityConfig;
import org.nexters.jaknaesoserver.domain.auth.service.AuthFacadeService;
import org.nexters.jaknaesoserver.domain.auth.service.JwtParser;
import org.nexters.jaknaesoserver.domain.auth.service.JwtProvider;
import org.nexters.jaknaesoserver.domain.auth.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@AutoConfigureRestDocs
@ImportAutoConfiguration({TestSecurityConfig.class})
public abstract class ControllerTest {

  @Autowired protected MockMvc mockMvc;
  @Autowired protected ObjectMapper objectMapper;

  @MockitoBean protected AuthFacadeService authFacadeService;

  @MockitoBean protected MemberService memberService;

  @MockitoBean protected OauthService oauthService;

  @MockitoBean protected JwtService jwtService;

  @MockitoBean protected JwtParser jwtParser;

  @MockitoBean protected JwtProvider jwtProvider;

  @MockitoBean protected SurveyService surveyService;

  @MockitoBean protected CharacterService characterService;
}
