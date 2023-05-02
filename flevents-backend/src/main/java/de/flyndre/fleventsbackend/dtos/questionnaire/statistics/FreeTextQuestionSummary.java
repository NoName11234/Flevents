package de.flyndre.fleventsbackend.dtos.questionnaire.statistics;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FreeTextQuestionSummary implements QuestionSummary{
    List<String> answers = new ArrayList<>();
    public FreeTextQuestionSummary(){

    }
}
