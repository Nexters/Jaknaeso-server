package org.nexters.jaknaesocore.domain.survey.service.event;

import java.util.List;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.survey.model.KeywordScore;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;

public record CreateCharacterEvent(
    Member member,
    SurveyBundle bundle,
    List<KeywordScore> scores,
    List<SurveySubmission> submissions) {}
