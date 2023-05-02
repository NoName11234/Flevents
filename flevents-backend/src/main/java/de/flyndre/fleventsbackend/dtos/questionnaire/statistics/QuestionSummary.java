package de.flyndre.fleventsbackend.dtos.questionnaire.statistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class QuestionSummary {
    private enum QuestionType {FreeTextQuestion, SingleChoiceQuestion}
    private int userCount = 0;

}
