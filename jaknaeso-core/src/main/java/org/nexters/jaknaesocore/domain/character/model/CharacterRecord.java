package org.nexters.jaknaesocore.domain.character.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseTimeEntity;
import org.nexters.jaknaesocore.common.util.OrdinalFormatter;
import org.nexters.jaknaesocore.domain.member.model.Member;
import org.nexters.jaknaesocore.domain.survey.model.Keyword;
import org.nexters.jaknaesocore.domain.survey.model.SurveyBundle;
import org.nexters.jaknaesocore.domain.survey.model.SurveySubmission;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CharacterRecord extends BaseTimeEntity {

  private int ordinalNumber;
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

  @OneToMany(mappedBy = "characterRecord", cascade = CascadeType.PERSIST, orphanRemoval = true)
  private List<ValueReport> valueReports = new ArrayList<>();

  private CharacterRecord(
      final int ordinalNumber,
      final String characterNo,
      final Member member,
      final SurveyBundle surveyBundle,
      final LocalDate startDate) {
    this.ordinalNumber = ordinalNumber;
    this.characterNo = characterNo;
    this.member = member;
    this.surveyBundle = surveyBundle;
    this.startDate = startDate;
  }

  @Builder
  private CharacterRecord(
      final int ordinalNumber,
      final String characterNo,
      final ValueCharacter valueCharacter,
      final LocalDate startDate,
      final LocalDate endDate,
      final Member member,
      final SurveyBundle surveyBundle,
      final List<ValueReport> valueReports) {
    this.ordinalNumber = ordinalNumber;
    this.characterNo = characterNo;
    this.valueCharacter = valueCharacter;
    this.startDate = startDate;
    this.endDate = endDate;
    this.member = member;
    this.surveyBundle = surveyBundle;

    if (valueReports != null) {
      this.valueReports = valueReports;
      this.valueReports.forEach(it -> it.updateCharacterRecord(this));
    }
  }

  public static CharacterRecord of(
      final CharacterRecord previousRecord,
      final Member member,
      final SurveyBundle bundle,
      final LocalDate startDate) {
    final int ordinalNumber = previousRecord.getOrdinalNumber() + 1;
    return new CharacterRecord(
        ordinalNumber, OrdinalFormatter.getCharacterNo(ordinalNumber), member, bundle, startDate);
  }

  public static CharacterRecord ofFirst(
      final Member member,
      final SurveyBundle surveyBundle,
      final ValueCharacter valueCharacter,
      final List<ValueReport> valueReports,
      List<SurveySubmission> submissions) {
    final int first = 1;
    return new CharacterRecord(
        first,
        OrdinalFormatter.getCharacterNo(first),
        valueCharacter,
        submissions.getFirst().getSubmittedAt().toLocalDate(),
        submissions.getLast().getSubmittedAt().toLocalDate(),
        member,
        surveyBundle,
        valueReports);
  }

  public void updateRecord(
      final ValueCharacter valueCharacter,
      final List<ValueReport> valueReports,
      final List<SurveySubmission> submissions) {
    this.valueCharacter = valueCharacter;
    this.startDate = submissions.getFirst().getSubmittedAt().toLocalDate();
    this.endDate = submissions.getLast().getSubmittedAt().toLocalDate();
    if (valueReports != null) {
      this.valueReports = valueReports;
      this.valueReports.forEach(it -> it.updateCharacterRecord(this));
    }
  }

  public Keyword getKeyword() {
    return valueCharacter.getKeyword();
  }

  public String getName() {
    return valueCharacter.getName();
  }

  public String getDescription() {
    return valueCharacter.getDescription();
  }

  public List<CharacterTrait> getTraitsByType(final CharacterTraitType characterTraitType) {
    return valueCharacter.getTraitsByType(characterTraitType);
  }

  public boolean isCompleted() {
    return valueCharacter != null && valueCharacter.getId() != null;
  }
}
