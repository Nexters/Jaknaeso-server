package org.nexters.jaknaesoserver.domain.auth.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesoserver.common.support.ControllerTest;
import org.nexters.jaknaesoserver.domain.auth.controller.dto.AppleLoginRequest;
import org.nexters.jaknaesoserver.domain.auth.controller.dto.KakaoLoginRequest;
import org.nexters.jaknaesoserver.domain.auth.controller.dto.KakaoLoginWithTokenRequest;
import org.nexters.jaknaesoserver.domain.auth.dto.TokenResponse;
import org.springframework.security.test.context.support.WithMockUser;

class AuthControllerTest extends ControllerTest {

  @Test
  void 애플_identityToken을_통해_서비스에_로그인한다() throws Exception {
    AppleLoginRequest request = new AppleLoginRequest("123456", "홍길동");

    given(authFacadeService.appleLogin(request.toServiceDto()))
        .willReturn(new TokenResponse(1L, "accessToken", "refreshToken"));

    mockMvc
        .perform(
            post("/api/v1/auth/apple-login")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "apple-login-success",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("애플 로그인 및 토큰 발급")
                        .tags("Auth Domain")
                        .requestFields(
                            fieldWithPath("idToken")
                                .type(SimpleType.STRING)
                                .description("애플 idToken"),
                            fieldWithPath("name").type(SimpleType.STRING).description("이름"))
                        .responseFields(
                            fieldWithPath("result")
                                .type(SimpleType.STRING)
                                .description("API 요청 결과 (성공/실패)"),
                            fieldWithPath("data.memberId")
                                .type(SimpleType.NUMBER)
                                .description("유저 ID"),
                            fieldWithPath("data.accessToken")
                                .type(SimpleType.STRING)
                                .description("액세스 토큰"),
                            fieldWithPath("data.refreshToken")
                                .type(SimpleType.STRING)
                                .description("리프레시 토큰"),
                            fieldWithPath("error").description("에러").optional())
                        .requestSchema(schema("AppleLoginRequest"))
                        .responseSchema(schema("TokenResponse"))
                        .build())));
  }

  @Test
  void 인가_코드로_카카오_API를_호출하여_서비스에_로그인한다() throws Exception {
    KakaoLoginRequest request = new KakaoLoginRequest("authorization code", "redirect-uri");

    given(authFacadeService.kakaoLogin(request.toServiceDto()))
        .willReturn(new TokenResponse(1L, "accessToken", "refreshToken"));

    mockMvc
        .perform(
            post("/api/v1/auth/kakao-login")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "kakao-login-success",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("카카오 로그인 및 토큰 발급")
                        .tags("Auth Domain")
                        .requestFields(
                            fieldWithPath("code").type(SimpleType.STRING).description("카카오 인증 코드"),
                            fieldWithPath("redirectUri")
                                .type(SimpleType.STRING)
                                .description("카카오 로그인 리다이렉트 URI"))
                        .responseFields(
                            fieldWithPath("result")
                                .type(SimpleType.STRING)
                                .description("API 요청 결과 (성공/실패)"),
                            fieldWithPath("data.memberId")
                                .type(SimpleType.NUMBER)
                                .description("유저 ID"),
                            fieldWithPath("data.accessToken")
                                .type(SimpleType.STRING)
                                .description("액세스 토큰"),
                            fieldWithPath("data.refreshToken")
                                .type(SimpleType.STRING)
                                .description("리프레시 토큰"),
                            fieldWithPath("error").description("에러").optional())
                        .requestSchema(schema("KakaoLoginRequest"))
                        .responseSchema(schema("TokenResponse"))
                        .build())));
  }

  @Test
  void 토큰으로_카카오_API를_호출하여_서비스에_로그인한다() throws Exception {
    KakaoLoginWithTokenRequest request = new KakaoLoginWithTokenRequest("access token");

    given(authFacadeService.kakaoLoginWithToken(request.toServiceDto()))
        .willReturn(new TokenResponse(1L, "accessToken", "refreshToken"));

    mockMvc
        .perform(
            post("/api/v1/auth/kakao-login/token")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "kakao-login-with-token-success",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("카카오 로그인 (for 안드로이드)")
                        .tags("Auth Domain")
                        .requestFields(
                            fieldWithPath("accessToken")
                                .type(SimpleType.STRING)
                                .description("카카오 액세스 토큰"))
                        .responseFields(
                            fieldWithPath("result")
                                .type(SimpleType.STRING)
                                .description("API 요청 결과 (성공/실패)"),
                            fieldWithPath("data.memberId")
                                .type(SimpleType.NUMBER)
                                .description("유저 ID"),
                            fieldWithPath("data.accessToken")
                                .type(SimpleType.STRING)
                                .description("액세스 토큰"),
                            fieldWithPath("data.refreshToken")
                                .type(SimpleType.STRING)
                                .description("리프레시 토큰"),
                            fieldWithPath("error").description("에러").optional())
                        .requestSchema(schema("KakaoLoginWithTokenRequest"))
                        .responseSchema(schema("TokenResponse"))
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
                            fieldWithPath("data.memberId")
                                .type(SimpleType.NUMBER)
                                .description("유저 ID"),
                            fieldWithPath("data.accessToken")
                                .type(SimpleType.STRING)
                                .description("액세스 토큰"),
                            fieldWithPath("data.refreshToken")
                                .type(SimpleType.STRING)
                                .description("리프레시 토큰"),
                            fieldWithPath("error").description("에러").optional())
                        .responseSchema(schema("TokenResponse"))
                        .build())));
  }
}
