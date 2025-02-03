package org.nexters.jaknaesoserver.domain.member.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.domain.member.service.dto.MemberResponse;
import org.nexters.jaknaesoserver.common.support.ControllerTest;
import org.nexters.jaknaesoserver.common.support.WithMockCustomUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

class MemberControllerTest extends ControllerTest {

  @WithMockCustomUser
  @Test
  void 서비스를_탈퇴한다() throws Exception {

    willDoNothing().given(memberService).deleteMember(1L);

    mockMvc
        .perform(
            delete("/api/v1/members/1")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "delete-member-success",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("회원 탈퇴")
                        .tags("Member Domain")
                        .build())));
  }

  @WithMockCustomUser
  @Test
  void 회원_정보를_조회한다() throws Exception {

    given(memberService.getMember(1L)).willReturn(new MemberResponse("홍길동", "test@example.com"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/members/1")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "get-member-success",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("회원 정보 조회")
                        .tags("Member Domain")
                        .responseFields(
                            fieldWithPath("result")
                                .type(SimpleType.STRING)
                                .description("API 요청 결과 (성공/실패)"),
                            fieldWithPath("data.name").type(SimpleType.STRING).description("회원 이름"),
                            fieldWithPath("data.email")
                                .type(SimpleType.STRING)
                                .description("회원 이메일"),
                            fieldWithPath("error").description("에러").optional())
                        .responseSchema(schema("MemberResponse"))
                        .build())));
  }
}
