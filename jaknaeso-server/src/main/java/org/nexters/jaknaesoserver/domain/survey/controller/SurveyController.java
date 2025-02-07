package org.nexters.jaknaesoserver.domain.survey.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.response.ApiResponse;
import org.nexters.jaknaesocore.domain.survey.dto.*;
import org.nexters.jaknaesocore.domain.survey.service.SurveyService;
import org.nexters.jaknaesoserver.domain.auth.model.CustomUserDetails;
import org.nexters.jaknaesoserver.domain.survey.controller.dto.SurveySubmissionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/surveys")
public class SurveyController {

  private final SurveyService surveyService;

  @GetMapping("/{bundleId}")
  public ApiResponse<SurveyResponse> getNextSurvey(
      @AuthenticationPrincipal CustomUserDetails member, @PathVariable Long bundleId) {
    return ApiResponse.success(surveyService.getNextSurvey(bundleId, member.getMemberId()));
  }

  @GetMapping("/history")
  public ApiResponse<SurveyHistoryResponse> getSurveyHistory(
      @AuthenticationPrincipal CustomUserDetails member) {
    return ApiResponse.success(surveyService.getSurveyHistory(member.getMemberId()));
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PostMapping("/{surveyId}/submission")
  public ApiResponse<?> submitSurvey(
      @AuthenticationPrincipal CustomUserDetails member,
      @PathVariable Long surveyId,
      @Valid @RequestBody SurveySubmissionRequest request) {
    LocalDateTime submittedAt = LocalDateTime.now();
    SurveySubmissionCommand command =
        SurveySubmissionCommand.builder()
            .surveyId(surveyId)
            .memberId(member.getMemberId())
            .optionId(request.optionId())
            .comment(request.comment())
            .build();
    surveyService.submitSurvey(command, submittedAt);
    return ApiResponse.success();
  }

  @GetMapping("/history/{bundleId}/submissions/{memberId}")
  public ApiResponse<SurveySubmissionHistoryResponse> getSurveyHistoryByMemberId(
      @PathVariable Long bundleId, @PathVariable Long memberId) {
    SurveySubmissionHistoryCommand command =
        SurveySubmissionHistoryCommand.builder().bundleId(bundleId).memberId(memberId).build();
    // BundleId 받아서 submissions를 그냥 가져오기
    // submissions record 안에 들어가야 할 정보
    // 번들 내에서 제출 회차, 제출 일자(연.월.일), 질문, 답변, 회고
    return ApiResponse.success(surveyService.getSurveySubmissionHistory(command));
  }
}
