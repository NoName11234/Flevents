package de.flyndre.fleventsbackend.dtos.questionaire;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * This Class is a model for the Single Choice Question.
 * It provides getter as well as setter.
 * @author Lukas Burkhardt
 * @version $I$
 */
@Getter
@Setter
public class SingleChoiceQuestion extends Question{
    private List<String> choices;
}
