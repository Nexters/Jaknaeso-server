package org.nexters.jaknaesocore.domain.character.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

  @ManyToOne
  @JoinColumn(name = "value_character_id")
  private ValueCharacter valueCharacter;

  private LocalDate startDate;

  private LocalDate endDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bundle_id")
  private SurveyBundle surveyBundle;

  @OneToMany(mappedBy = "characterRecord", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ValueReport> valueReports;

  @Builder
  private CharacterRecord(
      final String characterNo,
      final ValueCharacter valueCharacter,
      final LocalDate startDate,
      final LocalDate endDate,
      final Member member,
      final SurveyBundle surveyBundle) {
    this.characterNo = characterNo;
    this.valueCharacter = valueCharacter;
    this.startDate = startDate;
    this.endDate = endDate;
    this.member = member;
    this.surveyBundle = surveyBundle;
  }

  public void updateValueReports(final List<ValueReport> valueReports) {
    valueReports.forEach(it -> it.updateCharacterRecord(this));
    this.valueReports = valueReports;
  }
}
