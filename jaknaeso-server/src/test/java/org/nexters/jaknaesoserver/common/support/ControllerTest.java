package org.nexters.jaknaesoserver.common.support;

import org.nexters.jaknaesocore.domain.auth.service.OauthService;
import org.nexters.jaknaesocore.domain.sample.service.SampleService;
import org.nexters.jaknaesoserver.controller.SampleController;
import org.nexters.jaknaesoserver.domain.auth.service.AuthFacadeService;
import org.nexters.jaknaesoserver.domain.auth.service.JwtParser;
import org.nexters.jaknaesoserver.domain.auth.service.JwtProvider;
import org.nexters.jaknaesoserver.domain.auth.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({SampleController.class})
@AutoConfigureRestDocs
public abstract class ControllerTest {

  @Autowired protected MockMvc mockMvc;

  @MockitoBean protected AuthFacadeService authFacadeService;

  @MockitoBean protected OauthService oauthService;

  @MockitoBean protected JwtService jwtService;

  @MockitoBean protected SampleService sampleService;

  @MockitoBean protected JwtParser jwtParser;

  @MockitoBean protected JwtProvider jwtProvider;
}
