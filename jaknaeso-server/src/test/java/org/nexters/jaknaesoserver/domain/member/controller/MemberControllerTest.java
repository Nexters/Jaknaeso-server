package org.nexters.jaknaesoserver.domain.member.controller;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.common.support.error.CustomException;
import org.nexters.jaknaesocore.common.support.error.ErrorType;
import org.nexters.jaknaesoserver.common.support.ControllerTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;

class MemberControllerTest extends ControllerTest {

  @WithMockUser
  @Test
  void 서비스를_탈퇴한다() throws Exception {

    willDoNothing().given(memberService).deleteMember(1L);

    mockMvc
        .perform(
            delete("/api/v1/member")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .with(
                    authentication(
                        new TestingAuthenticationToken(1L, null, Collections.emptyList())))
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

  @WithMockUser
  @Test
  void 아이디에_해당하는_멤버가_존재하지_않아_404_얘외를_반환한다() throws Exception {

    willThrow(new CustomException(ErrorType.MEMBER_NOT_FOUND))
        .given(memberService)
        .deleteMember(any());

    mockMvc
        .perform(
            delete("/api/v1/member")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .with(
                    authentication(
                        new TestingAuthenticationToken(1L, null, Collections.emptyList())))
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
}
