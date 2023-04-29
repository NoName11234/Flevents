package de.flyndre.fleventsbackend.dtos.questionnaire.statistics;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Statistics {
    private List<QuestionSummary> questionSummaries;
}
