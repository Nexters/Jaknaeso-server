package org.nexters.jaknaesoserver.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesoserver.common.support.ControllerTest;
import org.nexters.jaknaesoserver.domain.auth.dto.TokenResponse;
import org.springframework.security.test.context.support.WithMockUser;

class AuthControllerTest extends ControllerTest {

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
