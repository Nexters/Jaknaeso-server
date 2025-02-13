package org.nexters.jaknaesocore.domain.character.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseTimeEntity;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CharacterRecord extends BaseTimeEntity {

  private String characterNo;

  // TODO: 추후 enum으로 변경
  private String characterType;

  private LocalDate startDate;

  private LocalDate endDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bundle_id")
  private SurveyBundle surveyBundle;

  @ElementCollection
  @CollectionTable(name = "value_reports", joinColumns = @JoinColumn(name = "character_id"))
  private List<ValueReport> valueReports;

  @Builder
  private CharacterRecord(
      final String characterNo,
      final String characterType,
      final LocalDate startDate,
      final LocalDate endDate,
      final Member member,
      final SurveyBundle surveyBundle,
      final List<ValueReport> valueReports) {
    this.characterNo = characterNo;
    this.characterType = characterType;
    this.startDate = startDate;
    this.endDate = endDate;
    this.member = member;
    this.surveyBundle = surveyBundle;
    this.valueReports = valueReports;
  }
}
