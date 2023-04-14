package de.flyndre.fleventsbackend.dtos.questionaire;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class AnsweredQuestionnaire {
    private String uuid;
    private String userId;
    private String questionnaire;
    private List<AnsweredQuestion> answers;
}
