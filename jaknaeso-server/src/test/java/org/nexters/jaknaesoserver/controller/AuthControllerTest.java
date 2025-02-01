package org.nexters.jaknaesoserver.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.domain.auth.service.dto.KakaoLoginCommand;
import org.nexters.jaknaesoserver.common.support.ControllerTest;
import org.nexters.jaknaesoserver.domain.auth.dto.TokenResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.client.RestClientException;

class AuthControllerTest extends ControllerTest {

  ObjectMapper objectMapper = new ObjectMapper();

  @WithMockUser
  @DisplayName("카카오 API를 호출하여 서비스에 로그인한다.")
  @Test
  void kakaoLoginSuccess() throws Exception {
    given(authFacadeService.kakaoLogin(new KakaoLoginCommand("authorization code")))
        .willReturn(new TokenResponse(1L, "accessToken", "refreshToken"));

    mockMvc
        .perform(
            get("/api/v1/auth/kakao-login")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .queryParam("code", "authorization code")
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "kakao-login-success",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("카카오 로그인 및 토큰 발급")
                        .tags("Auth Domain")
                        .queryParameters(
                            parameterWithName("code")
                                .type(SimpleType.STRING)
                                .description("카카오 인증 코드"))
                        .responseFields(
                            fieldWithPath("result")
                                .type(SimpleType.STRING)
                                .description("API 요청 결과 (성공/실패)"),
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

  @WithMockUser
  @DisplayName("카카오 API 호출이 실패하여 서비스 로그인에 실패하고 서버 오류를 반환한다.")
  @Test
  void kakaoLoginFail() throws Exception {
    given(authFacadeService.kakaoLogin(new KakaoLoginCommand("invalid authorization code")))
        .willThrow(RestClientException.class);

    mockMvc
        .perform(
            get("/api/v1/auth/kakao-login")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .queryParam("code", "invalid authorization code")
                .with(csrf()))
        .andExpect(status().is5xxServerError())
        .andDo(
            document(
                "kakao-login-fail",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("카카오 로그인 및 토큰 발급")
                        .tags("Auth Domain")
                        .queryParameters(
                            parameterWithName("code")
                                .type(SimpleType.STRING)
                                .description("카카오 인증 코드"))
                        .responseFields(
                            fieldWithPath("result")
                                .type(SimpleType.STRING)
                                .description("API 요청 결과 (성공/실패)"),
                            fieldWithPath("error.code")
                                .type(SimpleType.STRING)
                                .description("커스텀 에러 코드"),
                            fieldWithPath("error.message")
                                .type(SimpleType.STRING)
                                .description("에러 상세 메시지"),
                            fieldWithPath("error.data").description("에러 관련 데이터").optional(),
                            fieldWithPath("data").description("정상적인 요청의 결과 데이터").optional())
                        .build())));
  }

  @WithMockUser
  @DisplayName("리프레시 토큰을 통해 토큰을 재발급한다.")
  @Test
  void reissueToken() throws Exception {
    given(authFacadeService.reissueToken(any()))
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
