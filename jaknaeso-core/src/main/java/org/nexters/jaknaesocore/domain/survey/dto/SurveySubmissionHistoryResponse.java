package org.nexters.jaknaesocore.domain.survey.dto;

import java.util.List;
import org.nexters.jaknaesocore.domain.survey.model.SurveyRecord;

public record SurveySubmissionHistoryResponse(List<SurveyRecord> surveyRecords) {}
