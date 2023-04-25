package de.flyndre.fleventsbackend.dtos.questionnaire;

import de.flyndre.fleventsbackend.Models.questionnaire.Choice;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class AnsweredQuestion {
    private String uuid;
    private enum QuestionType {FreeTextQuestion, SingleChoiceQuestion}
    @Nullable
    private Choice choice;
    @Nullable
    private String answer;
}
