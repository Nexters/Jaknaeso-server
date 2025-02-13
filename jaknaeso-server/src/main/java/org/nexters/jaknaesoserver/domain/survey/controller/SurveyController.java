package org.nexters.jaknaesoserver.domain.survey.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.error.CustomException;
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

  @GetMapping("/members/{memberId}/submissions")
  public ApiResponse<SurveySubmissionHistoryResponse> getSurveyHistoryByMemberId(
      @RequestParam Long bundleId,
      @PathVariable Long memberId,
      @AuthenticationPrincipal CustomUserDetails member) {
    if (!member.getMemberId().equals(memberId)) {
      throw CustomException.FORBIDDEN_ACCESS;
    }
    SurveySubmissionHistoryCommand command =
        SurveySubmissionHistoryCommand.builder().bundleId(bundleId).memberId(memberId).build();
    return ApiResponse.success(surveyService.getSurveySubmissionHistory(command));
  }

  @GetMapping("/onboarding")
  public ApiResponse<OnboardingSurveyResponse> getOnboardingSurvey() {
    return ApiResponse.success(surveyService.getOnboardingSurveys());
  }
}
