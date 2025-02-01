package org.nexters.jaknaesoserver.controller;

import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.response.ApiResponse;
import org.nexters.jaknaesocore.domain.member.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@RestController
public class MemberController {

  private final MemberService memberService;

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public ApiResponse<?> deleteMember(@AuthenticationPrincipal Long userId) {
    memberService.deleteMember(userId);
    return ApiResponse.success();
  }
}
