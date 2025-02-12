package org.nexters.jaknaesocore.domain.character.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import org.nexters.jaknaesocore.domain.character.model.ValueReport;

@Builder
public record CharacterReportResponse(
    String type,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    @JsonSerialize List<ValueReport> valueReports) {}
