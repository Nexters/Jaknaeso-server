package org.nexters.jaknaesoserver.domain.member.controller;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.common.support.error.ErrorType;
import org.nexters.jaknaesoserver.common.support.ControllerTest;
import org.nexters.jaknaesoserver.common.support.WithMockCustomUser;

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
            MockMvcRestDocumentationWrapper.document(
                "delete-member-success",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("회원 탈퇴")
                        .tags("Member Domain")
                        .build())));
  }

  @WithMockCustomUser
  @Test
  void 아이디에_해당하는_멤버가_존재하지_않아_404_얘외를_반환한다() throws Exception {

    willThrow(new CustomException(ErrorType.MEMBER_NOT_FOUND))
        .given(memberService)
        .deleteMember(1L);

    mockMvc
        .perform(
            delete("/api/v1/members/1")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .with(csrf()))
        .andExpect(status().isNotFound())
        .andDo(
            MockMvcRestDocumentationWrapper.document(
                "delete-member-fail",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("회원 탈퇴")
                        .tags("Member Domain")
                        .build())));
  }

  @WithMockCustomUser(userId = 1)
  @Test
  void 본인_계정이_아닌_계정을_삭제하려고_하면_예외를_반환한다() throws Exception {

    mockMvc
        .perform(
            delete("/api/v1/members/2")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .with(csrf()))
        .andExpect(status().is4xxClientError())
        .andDo(
            MockMvcRestDocumentationWrapper.document(
                "delete-member-fail",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("회원 탈퇴")
                        .tags("Member Domain")
                        .build())));
  }
}
