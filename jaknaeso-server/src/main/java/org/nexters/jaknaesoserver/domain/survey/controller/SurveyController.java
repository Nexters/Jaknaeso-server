package org.nexters.jaknaesoserver.domain.survey.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.response.ApiResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyHistoryResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyResponse;
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
    surveyService.submitSurvey(
        surveyId, member.getMemberId(), request.toServiceRequest(), submittedAt);
    return ApiResponse.success();
  }
}
