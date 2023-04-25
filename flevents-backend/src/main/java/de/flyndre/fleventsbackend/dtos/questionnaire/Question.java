package de.flyndre.fleventsbackend.dtos.questionnaire;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
@Setter
public class Question {
    private String uuid;
    private String question;
    private enum QuestionType {FreeTextQuestion, SingleChoiceQuestion}
    @Nullable
    private List<Choice> choices;


}
