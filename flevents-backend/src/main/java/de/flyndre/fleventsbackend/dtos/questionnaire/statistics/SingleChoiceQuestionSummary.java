package de.flyndre.fleventsbackend.dtos.questionnaire.statistics;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SingleChoiceQuestionSummary extends QuestionSummary{
    private List<Integer> votes = new ArrayList<Integer>();
    QuestionType questionType = QuestionType.SingleTypeQuestion;
}
