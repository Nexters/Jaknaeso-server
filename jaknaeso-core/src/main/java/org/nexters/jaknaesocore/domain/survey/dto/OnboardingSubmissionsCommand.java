package org.nexters.jaknaesocore.domain.survey.dto;

import java.util.List;

public record OnboardingSubmissionsCommand(
    List<OnboardingSubmissionResult> submissions, Long memberId) {}
