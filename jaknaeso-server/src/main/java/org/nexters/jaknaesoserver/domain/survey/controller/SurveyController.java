package org.nexters.jaknaesoserver.domain.survey.controller;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.response.ApiResponse;
import org.nexters.jaknaesocore.domain.survey.dto.SurveyResponse;
import org.nexters.jaknaesocore.domain.survey.service.SurveyService;
import org.nexters.jaknaesoserver.domain.auth.model.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
