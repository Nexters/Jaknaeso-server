package org.nexters.jaknaesocore.domain.character.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexters.jaknaesocore.common.model.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CharacterValueReport extends BaseTimeEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "character_record_id")
  private CharacterRecord characterRecord;

  // TODO: 추후 대체
  //  @OneToMany(mappedBy = "characterValueReport", cascade = CascadeType.ALL, orphanRemoval = true)
  @ElementCollection
  @CollectionTable(
      name = "value_reports",
      joinColumns = @JoinColumn(name = "character_value_report_id"))
  private List<ValueReport> valueReports;

  public CharacterValueReport(
      final CharacterRecord characterRecord, final List<ValueReport> valueReports) {
    this.characterRecord = characterRecord;
    this.valueReports = valueReports;
    characterRecord.updateCharacterValueReport(this);
  }
}
