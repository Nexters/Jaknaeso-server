package org.nexters.jaknaesocore.domain.character.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.nexters.jaknaesocore.domain.character.model.ValueReport;

public record CharacterValueReportResponse(@JsonSerialize List<ValueReport> valueReports) {}
