package org.nexters.jaknaesoserver.domain.member.controller;

import java.nio.file.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.common.support.response.ApiResponse;
import org.nexters.jaknaesocore.domain.member.service.MemberService;
import org.nexters.jaknaesocore.domain.member.service.dto.MemberResponse;
import org.nexters.jaknaesoserver.domain.auth.model.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@RestController
public class MemberController {

  private final MemberService memberService;

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{memberId}")
  public ApiResponse<?> deleteMember(
      @PathVariable Long memberId, @AuthenticationPrincipal CustomUserDetails member)
      throws AccessDeniedException {
    if (!memberId.equals(member.getMemberId())) {
      throw new AccessDeniedException("본인의 계정만 삭제할 수 있습니다.");
    }
    memberService.deleteMember(memberId);
    return ApiResponse.success();
  }

  @GetMapping("/{memberId}")
  public ApiResponse<MemberResponse> getMember(@PathVariable Long memberId) {
    MemberResponse response = memberService.getMember(memberId);
    return ApiResponse.success(response);
  }
}
