package org.nexters.jaknaesocore.domain.character.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.nexters.jaknaesocore.domain.character.service.dto.CharacterResponse;
import org.nexters.jaknaesocore.domain.member.repository.MemberRepository;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.repository.SurveySubmissionRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CharacterService {

  private final MemberRepository memberRepository;
  private final SurveySubmissionRepository surveySubmissionRepository;

  public List<CharacterResponse> getCharacters(final Long memberId) {
    memberRepository.findMember(memberId);

    AtomicLong ordinalNumber = new AtomicLong(1);
    return getSurveyBundles(memberId).stream()
        .map(it -> new CharacterResponse(ordinalNumber.getAndIncrement(), it.getId()))
        .toList();
  }

  private List<SurveyBundle> getSurveyBundles(final Long memberId) {
    return surveySubmissionRepository
        .findWithSurveyBundlesByMemberIdAndDeletedAtIsNull(memberId)
        .stream()
        .map(it -> it.getSurvey().getSurveyBundle())
        .toList();
  }
}
