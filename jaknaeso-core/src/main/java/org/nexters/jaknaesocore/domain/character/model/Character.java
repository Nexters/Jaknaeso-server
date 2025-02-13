package org.nexters.jaknaesocore.domain.character.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
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
public class Character extends BaseTimeEntity {

  private String characterNo;

  private String type;

  private String description;

  private LocalDate startDate;

  private LocalDate endDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bundle_id")
  private SurveyBundle surveyBundle;

  @OneToOne(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
  private CharacterValueReport characterValueReport;

  @Builder
  private Character(
      final String characterNo,
      final CharacterType characterType,
      final LocalDate startDate,
      final LocalDate endDate,
      final Member member,
      final SurveyBundle surveyBundle) {
    this.characterNo = characterNo;
    this.startDate = startDate;
    this.endDate = endDate;
    this.member = member;
    this.surveyBundle = surveyBundle;
    setCharacterType(characterType);
  }

  private void setCharacterType(final CharacterType characterType) {
    this.type = characterType.getName();
    this.description = characterType.getDescription();
  }

  public void updateCharacterValueReport(final CharacterValueReport characterValueReport) {
    this.characterValueReport = characterValueReport;
  }
}
