package de.flyndre.fleventsbackend.dtos.questionnaire;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnsweredQuestionnaire {
    private String uuid;
    private String userId;
    private String questionnaireId;
    private List<AnsweredQuestion> answers;
}
