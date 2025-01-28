package org.nexters.jaknaesoserver.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.domain.auth.dto.LoginKakaoRequest;
import org.nexters.jaknaesocore.domain.auth.restclient.KakaoClient;
import org.nexters.jaknaesocore.domain.auth.restclient.dto.KakaoUserInfoResponse;
import org.nexters.jaknaesocore.domain.auth.service.AuthService;
import org.nexters.jaknaesoserver.common.support.ControllerTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestClientException;
import org.nexters.jaknaesoserver.domain.auth.dto.TokenResponse;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(AuthController.class)
@Import({AuthService.class})
class AuthControllerTest extends ControllerTest {

  @MockitoBean
  KakaoClient kakaoClient;

  ObjectMapper objectMapper = new ObjectMapper();

  @DisplayName("카카오 API를 호출하여 사용자의 정보를 정상적으로 조회한다.")
  @Test
  void loginKakao() throws Exception {
    LoginKakaoRequest request = new LoginKakaoRequest("access token");

    given(kakaoClient.requestUserInfo(anyString(), anyString()))
        .willReturn(new KakaoUserInfoResponse(1L));

    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/kakao/login")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andDo(document("oauth-login-kakao-success",
            resource(
                ResourceSnippetParameters.builder()
                    .tags("Auth Domain")
                    .requestFields(
                        fieldWithPath("accessToken").type(SimpleType.STRING).description("액세스 토큰")
                    ).build()
            )
        ));
  }

  @DisplayName("잘못된 Authorization 헤더로 인해 카카오 API 호출이 실패하면 서버 오류를 반환한다.")
  @Test
  void loginKakaoWithInvalidAuthorizationHeader() throws Exception {
    LoginKakaoRequest request = new LoginKakaoRequest("invalid access token");

    given(kakaoClient.requestUserInfo(anyString(), anyString()))
        .willThrow(new RestClientException("인증에 실패했습니다. 카카오 API 토큰을 확인해주세요."));

    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/kakao/login")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().is5xxServerError())
        .andDo(document("oauth-login-kakao-fail",
            resource(
                ResourceSnippetParameters.builder()
                    .tags("Auth Domain")
                    .requestFields(
                        fieldWithPath("accessToken").type(SimpleType.STRING).description("액세스 토큰")
                    ).build()
            )
        ));
  }

  @WithMockUser
  @DisplayName("리프레시토큰을 통해 토큰을 재발급한다.")
  @Test
  void reissueToken() throws Exception {
    given(jwtService.reissueToken(any()))
        .willReturn(new TokenResponse(1L, "accessToken", "refreshToken"));

    mockMvc
        .perform(
            post("/api/v1/auth/reissue")
                .header("Refresh-Token", "Bearer refreshToken")
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "token-reissue",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("리프레시 토큰을 통해 토큰 재발급")
                        .tag("Auth Domain")
                        .requestHeaders(headerWithName("Refresh-Token").description("리프레시 토큰"))
                        .responseFields(
                            fieldWithPath("result").type(SimpleType.STRING).description("결과"),
                            fieldWithPath("data.userId")
                                .type(SimpleType.NUMBER)
                                .description("유저 ID"),
                            fieldWithPath("data.accessToken")
                                .type(SimpleType.STRING)
                                .description("액세스 토큰"),
                            fieldWithPath("data.refreshToken")
                                .type(SimpleType.STRING)
                                .description("리프레시 토큰"),
                            fieldWithPath("error").description("에러").optional())
                        .build())));
  }
}