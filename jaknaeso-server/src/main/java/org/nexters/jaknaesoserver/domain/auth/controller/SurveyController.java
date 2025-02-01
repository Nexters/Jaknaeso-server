package org.nexters.jaknaesoserver.controller;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.response.ApiResponse;
import org.nexters.jaknaesocore.domain.question.dto.SurveyResponse;
import org.nexters.jaknaesocore.domain.question.service.SurveyService;
import org.nexters.jaknaesoserver.domain.auth.model.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/survey")
public class SurveyController {

  private final SurveyService surveyService;

  @GetMapping("/my/{bundleId}")
  public ApiResponse<SurveyResponse> getSurvey(
      @AuthenticationPrincipal CustomUserDetails member, @PathVariable Long bundleId) {
    return ApiResponse.success(surveyService.getSurvey(bundleId, member.getMemberId()));
  }
}
