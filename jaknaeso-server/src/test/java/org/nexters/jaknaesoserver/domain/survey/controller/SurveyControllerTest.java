package org.nexters.jaknaesoserver.domain.survey.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.nexters.jaknaesocore.domain.survey.dto.*;
import org.nexters.jaknaesocore.domain.survey.model.SurveyRecord;
import org.nexters.jaknaesoserver.common.support.ControllerTest;
import org.nexters.jaknaesoserver.common.support.WithMockCustomUser;
import org.nexters.jaknaesoserver.domain.survey.controller.dto.OnboardingSubmissionInfoRequest;
import org.nexters.jaknaesoserver.domain.survey.controller.dto.OnboardingSubmissionRequest;
import org.nexters.jaknaesoserver.domain.survey.controller.dto.SurveySubmissionRequest;
import org.springframework.http.MediaType;

class SurveyControllerTest extends ControllerTest {

  @WithMockCustomUser
  @DisplayName("번들을 통해 번들 내부의 랜덤한 설문지를 가져온다.")
  @Test
  void getNextSurvey() throws Exception {

    SurveyResponse response =
        new SurveyResponse(
            1L,
            "대학 동기 모임에서 나의 승진 이야기가 나왔습니다" + "나의 행복 지수는" + "회사에서 팀 리더로 뽑혔습니다" + "나는 노는게 좋다.",
            "MULTIPLE_CHOICE",
            List.of(
                new SurveyOptionsResponse(1L, "1점"),
                new SurveyOptionsResponse(2L, "2점"),
                new SurveyOptionsResponse(3L, "3점"),
                new SurveyOptionsResponse(4L, "4점"),
                new SurveyOptionsResponse(5L, "5점")));

    given(surveyService.getNextSurvey(anyLong(), anyLong())).willReturn(response);

    mockMvc
        .perform(get("/api/v1/surveys/{bundleId}", 1L).with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "survey-get-by-bundle-id",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("번들 내부의 랜덤한 설문지를 조회")
                        .tag("Survey Domain")
                        .pathParameters(
                            parameterWithName("bundleId")
                                .type(SimpleType.NUMBER)
                                .description("번들 ID"))
                        .responseFields(
                            fieldWithPath("result").type(SimpleType.STRING).description("결과"),
                            fieldWithPath("data.id").type(SimpleType.NUMBER).description("설문지 ID"),
                            fieldWithPath("data.contents")
                                .type(SimpleType.STRING)
                                .description("설문 내용"),
                            fieldWithPath("data.surveyType")
                                .type(SimpleType.STRING)
                                .description("설문 타입"),
                            fieldWithPath("data.options[].id")
                                .type(SimpleType.NUMBER)
                                .description("설문지 옵션 ID"),
                            fieldWithPath("data.options[].optionContents")
                                .type(SimpleType.STRING)
                                .description("설문지 옵션 내용"),
                            fieldWithPath("error").description("에러").optional())
                        .responseSchema(Schema.schema("surveyResponse"))
                        .build())));
  }

  @WithMockCustomUser
  @DisplayName("회원의 설문 이력을 가져온다.")
  @Test
  void getSurveyHistory() throws Exception {
    SurveyHistoryResponse response =
        new SurveyHistoryResponse(
            1L,
            List.of(new SurveyHistoryDetailResponse(1L, 1), new SurveyHistoryDetailResponse(2L, 1)),
            3,
            false);

    given(surveyService.getSurveyHistory(anyLong())).willReturn(response);

    mockMvc
        .perform(get("/api/v1/surveys/history").with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "survey-get-history",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("회원의 설문 이력을 조회")
                        .tag("Survey Domain")
                        .responseFields(
                            fieldWithPath("result").type(SimpleType.STRING).description("결과"),
                            fieldWithPath("data.bundleId")
                                .type(SimpleType.NUMBER)
                                .description("번들 ID"),
                            fieldWithPath("data.surveyHistoryDetails[].submissionId")
                                .type(SimpleType.NUMBER)
                                .description("제출 ID"),
                            fieldWithPath("data.surveyHistoryDetails[].index")
                                .type(SimpleType.NUMBER)
                                .description("설문 회차 정보 인덱스 값"),
                            fieldWithPath("data.nextSurveyIndex")
                                .type(SimpleType.NUMBER)
                                .description("다음 설문 인덱스"),
                            fieldWithPath("data.isCompleted")
                                .type(SimpleType.BOOLEAN)
                                .description("설문 완료 여부"),
                            fieldWithPath("error").description("에러").optional())
                        .responseSchema(Schema.schema("surveyHistoryResponse"))
                        .build())));
  }

  @WithMockCustomUser
  @Test
  void 설문에_응답을_제출한다() throws Exception {
    SurveySubmissionRequest request = new SurveySubmissionRequest(1L, "나는 행복해요");
    willDoNothing()
        .given(surveyService)
        .submitSurvey(any(SurveySubmissionCommand.class), any(LocalDateTime.class));

    mockMvc
        .perform(
            post("/api/v1/surveys/{surveyId}/submission", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "survey-submit",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("설문에 응답을 제출")
                        .tag("Survey Domain")
                        .pathParameters(
                            parameterWithName("surveyId")
                                .type(SimpleType.NUMBER)
                                .description("설문 ID"))
                        .requestFields(
                            fieldWithPath("optionId")
                                .type(SimpleType.NUMBER)
                                .description("설문지 옵션 ID"),
                            fieldWithPath("comment")
                                .type(SimpleType.STRING)
                                .description("추가 응답 내용"))
                        .responseFields(
                            fieldWithPath("result").type(SimpleType.STRING).description("결과"),
                            fieldWithPath("data").description("데이터").optional(),
                            fieldWithPath("error").description("에러").optional())
                        .requestSchema(Schema.schema("surveySubmissionRequest"))
                        .responseSchema(Schema.schema("surveyResponse"))
                        .build())));
  }

  @WithMockCustomUser(userId = 1L)
  @Test
  void 설문_결과를_조회한다() throws Exception {
    // given
    SurveyRecord record1 =
        SurveyRecord.builder()
            .question("커리어를 향상시킬 수 있는 일자리이지만 가까운 사람들과 멀어져야한다면, 이 일자리를 선택하실 건가요?")
            .answer("주변 사람과 물리적으로 멀어지더라도, 커리어를 선택한다.")
            .retrospective(
                "가까운 사람들과 물리적으로 멀어지더라도 그 관계가 사라지진 않음.  내 노력에 따라 관계는 달라질 수 있지만 커리어 기회는 원할 때 오는게 아님")
            .submittedAt("2025.02.06")
            .build();
    SurveyRecord record2 =
        SurveyRecord.builder()
            .question("새로운 친구를 만나며 나의 삶에 변화를 주고 싶다. 나는 어떤 친구를 더 가까이 두고 싶을까?")
            .answer("감정적인 교류를 통해 서로를 깊이 이해하고 지지하는 친구")
            .submittedAt("2025.02.06")
            .build();
    SurveySubmissionHistoryResponse response =
        new SurveySubmissionHistoryResponse(List.of(record1, record2));

    given(surveyService.getSurveySubmissionHistory(any(SurveySubmissionHistoryCommand.class)))
        .willReturn(response);

    mockMvc
        .perform(
            get("/api/v1/surveys/members/{memberId}/submissions", 1L)
                .queryParam("bundleId", "1")
                .with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "survey-get-submission-history",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("설문 결과를 조회")
                        .tag("Survey Domain")
                        .pathParameters(
                            parameterWithName("memberId")
                                .type(SimpleType.NUMBER)
                                .description("회원 ID"))
                        .queryParameters(
                            parameterWithName("bundleId")
                                .type(SimpleType.NUMBER)
                                .description("번들 ID"))
                        .responseFields(
                            fieldWithPath("result").type(SimpleType.STRING).description("결과"),
                            fieldWithPath("data.surveyRecords").description("설문 이력"),
                            fieldWithPath("data.surveyRecords[].submissionId")
                                .type(SimpleType.NUMBER)
                                .description("제출 ID"),
                            fieldWithPath("data.surveyRecords[].question")
                                .type(SimpleType.STRING)
                                .description("질문"),
                            fieldWithPath("data.surveyRecords[].answer")
                                .type(SimpleType.STRING)
                                .description("답변"),
                            fieldWithPath("data.surveyRecords[].retrospective")
                                .type(SimpleType.STRING)
                                .optional()
                                .description("회고"),
                            fieldWithPath("data.surveyRecords[].submittedAt")
                                .type(SimpleType.STRING)
                                .description("제출 일자"),
                            fieldWithPath("error").description("에러").optional())
                        .responseSchema(Schema.schema("surveySubmissionHistoryResponse"))
                        .build())));
  }

  @WithMockCustomUser
  @Test
  void 설문_결과를_조회할_때_자신의_결과가_아니면_예외가_발생한다() throws Exception {
    SurveyRecord record1 =
        SurveyRecord.builder()
            .question("커리어를 향상시킬 수 있는 일자리이지만 가까운 사람들과 멀어져야한다면, 이 일자리를 선택하실 건가요?")
            .answer("주변 사람과 물리적으로 멀어지더라도, 커리어를 선택한다.")
            .retrospective(
                "가까운 사람들과 물리적으로 멀어지더라도 그 관계가 사라지진 않음.  내 노력에 따라 관계는 달라질 수 있지만 커리어 기회는 원할 때 오는게 아님")
            .submittedAt("2025.02.06")
            .build();
    SurveyRecord record2 =
        SurveyRecord.builder()
            .question("새로운 친구를 만나며 나의 삶에 변화를 주고 싶다. 나는 어떤 친구를 더 가까이 두고 싶을까?")
            .answer("감정적인 교류를 통해 서로를 깊이 이해하고 지지하는 친구")
            .submittedAt("2025.02.06")
            .build();
    SurveySubmissionHistoryResponse response =
        new SurveySubmissionHistoryResponse(List.of(record1, record2));

    given(surveyService.getSurveySubmissionHistory(any(SurveySubmissionHistoryCommand.class)))
        .willReturn(response);

    mockMvc
        .perform(
            get("/api/v1/surveys/members/{memberId}/submissions", 2L)
                .queryParam("bundleId", "1")
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @WithMockCustomUser
  @Test
  void 온보딩_설문을_조회한다() throws Exception {
    OnboardingSurveyResponse response =
        new OnboardingSurveyResponse(
            List.of(
                new SurveyResponse(
                    1L,
                    "새로운 아이디어를 갖고 창의적인 것이 그/그녀에게 중요하다. 그/그녀는 일을 자신만의 독특한 방식으로 하는 것을 좋아한다.",
                    "ONBOARDING",
                    List.of(
                        new SurveyOptionsResponse(1L, "전혀 나와 같지 않다."),
                        new SurveyOptionsResponse(2L, "나와 같지 않다."),
                        new SurveyOptionsResponse(3L, "나와 조금 같다."),
                        new SurveyOptionsResponse(4L, "나와 같다."),
                        new SurveyOptionsResponse(5L, "나와 매우 같다."))),
                new SurveyResponse(
                    2L,
                    "그/그녀에게 부자가 되는 것은 중요하다. 많은 돈과 비싼 물건들을 가지길 원한다.",
                    "ONBOARDING",
                    List.of(
                        new SurveyOptionsResponse(6L, "전혀 나와 같지 않다."),
                        new SurveyOptionsResponse(7L, "나와 같지 않다."),
                        new SurveyOptionsResponse(8L, "나와 조금 같다."),
                        new SurveyOptionsResponse(9L, "나와 같다."),
                        new SurveyOptionsResponse(10L, "나와 매우 같다."))),
                new SurveyResponse(
                    3L,
                    "세상의 모든 사람들이 평등하게 대우받아야 한다고 생각한다. 그/그녀는 모든 사람이 인생에서 동등한 기회를 가져야 한다고 믿는다.",
                    "ONBOARDING",
                    List.of(
                        new SurveyOptionsResponse(11L, "전혀 나와 같지 않다."),
                        new SurveyOptionsResponse(12L, "나와 같지 않다."),
                        new SurveyOptionsResponse(13L, "나와 조금 같다."),
                        new SurveyOptionsResponse(14L, "나와 같다."),
                        new SurveyOptionsResponse(15L, "나와 매우 같다."))),
                new SurveyResponse(
                    4L,
                    "그/그녀에게 자신의 능력을 보여주는 것이 매우 중요하다. 사람들이 자신이 하는 일을 인정해주길 바란다.",
                    "ONBOARDING",
                    List.of(
                        new SurveyOptionsResponse(16L, "전혀 나와 같지 않다."),
                        new SurveyOptionsResponse(17L, "나와 같지 않다."),
                        new SurveyOptionsResponse(18L, "나와 조금 같다."),
                        new SurveyOptionsResponse(19L, "나와 같다."),
                        new SurveyOptionsResponse(20L, "나와 매우 같다.")))));
    given(surveyService.getOnboardingSurveys()).willReturn(response);

    mockMvc
        .perform(
            get("/api/v1/surveys/onboarding").with(csrf()).header("Authorization", "Bearer token"))
        .andExpect(status().isOk())
        .andDo(
            document(
                "survey-get-onboarding",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("온보딩 설문 조회")
                        .tag("Survey Domain")
                        .requestHeaders(
                            headerWithName("Authorization")
                                .type(SimpleType.STRING)
                                .description("Bearer 토큰"))
                        .responseFields(
                            fieldWithPath("result").type(SimpleType.STRING).description("결과"),
                            fieldWithPath("data.surveyResponses").description("온보딩 설문"),
                            fieldWithPath("data.surveyResponses[].id")
                                .type(SimpleType.NUMBER)
                                .description("설문 ID"),
                            fieldWithPath("data.surveyResponses[].contents")
                                .type(SimpleType.STRING)
                                .description("설문 내용"),
                            fieldWithPath("data.surveyResponses[].surveyType")
                                .type(SimpleType.STRING)
                                .description("설문 타입"),
                            fieldWithPath("data.surveyResponses[].options[].id")
                                .type(SimpleType.NUMBER)
                                .description("설문지 옵션 ID"),
                            fieldWithPath("data.surveyResponses[].options[].optionContents")
                                .type(SimpleType.STRING)
                                .description("설문지 옵션 내용"),
                            fieldWithPath("error").description("에러").optional())
                        .responseSchema(Schema.schema("onboardingSurveyResponse"))
                        .build())));
  }

  @WithMockCustomUser
  @Test
  void 온보딩_설문에_응답을_제출한다() throws Exception {
    willDoNothing()
        .given(surveyService)
        .submitOnboardingSurvey(any(OnboardingSubmissionsCommand.class), any(LocalDateTime.class));

    OnboardingSubmissionRequest request =
        new OnboardingSubmissionRequest(
            List.of(
                new OnboardingSubmissionInfoRequest(1L, 1L),
                new OnboardingSubmissionInfoRequest(2L, 7L),
                new OnboardingSubmissionInfoRequest(3L, 13L)));

    mockMvc
        .perform(
            post("/api/v1/surveys/onboarding/submission")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer token")
                .with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "survey-submit-onboarding",
                resource(
                    ResourceSnippetParameters.builder()
                        .description("온보딩 설문 제출")
                        .tag("Survey Domain")
                        .requestHeaders(
                            headerWithName("Authorization")
                                .type(SimpleType.STRING)
                                .description("Bearer 토큰"))
                        .requestFields(
                            fieldWithPath("submissionsInfo[].surveyId")
                                .type(SimpleType.NUMBER)
                                .description("설문 ID"),
                            fieldWithPath("submissionsInfo[].optionId")
                                .type(SimpleType.NUMBER)
                                .description("설문지 선택지 ID"))
                        .responseFields(
                            fieldWithPath("result").type(SimpleType.STRING).description("결과"),
                            fieldWithPath("data").description("데이터").optional(),
                            fieldWithPath("error").description("에러").optional())
                        .requestSchema(Schema.schema("onboardingSubmissionRequest"))
                        .responseSchema(Schema.schema("surveyResponse"))
                        .build())));
  }
}
