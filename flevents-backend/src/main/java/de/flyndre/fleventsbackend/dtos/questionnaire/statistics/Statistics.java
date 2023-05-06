package de.flyndre.fleventsbackend.dtos.questionnaire.statistics;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Statistics {
    private List<QuestionSummary> questionSummaries;
    private int userCount = 0;

    public Statistics(){
        this.questionSummaries = new ArrayList<>();
    }
}
