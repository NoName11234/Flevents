package de.flyndre.fleventsbackend.dtos.questionaire;

import lombok.Getter;
import lombok.Setter;

/**
 * This Class is the Answer for a Single Choice Question.
 * It provides getter as well as setter.
 * @author Lukas Burkhardt
 * @version $I$
 */
@Getter
@Setter
public class AnsweredSingleChoiceQuestion extends AnsweredQuestion{
    private String choice;
}
