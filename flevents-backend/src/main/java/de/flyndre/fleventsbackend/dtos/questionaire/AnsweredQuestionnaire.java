package de.flyndre.fleventsbackend.dtos.questionaire;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * This Class is the Answer for a complete Questionnaire.
 * It provides getter as well as setter.
 * @author Lukas Burkhardt
 * @version $I$
 */
@Getter
@Setter
public class AnsweredQuestionnaire {
    private String uuid;
    private String userId;
    private String questionnaire;
    private List<AnsweredQuestion> answers;
}
