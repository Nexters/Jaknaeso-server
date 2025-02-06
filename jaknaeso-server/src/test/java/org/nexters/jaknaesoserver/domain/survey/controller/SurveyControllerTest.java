package org.nexters.jaknaesoserver.domain.survey.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
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
import org.nexters.jaknaesocore.domain.survey.dto.SurveyHistoryDetailResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyHistoryResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyOptionsResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveySubmissionCommand;
import org.nexters.jaknaesoserver.common.support.ControllerTest;
import org.nexters.jaknaesoserver.common.support.WithMockCustomUser;
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
            List.of(new SurveyHistoryDetailResponse(1L), new SurveyHistoryDetailResponse(2L)),
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
}
