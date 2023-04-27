package de.flyndre.fleventsbackend.dtos.questionaire;

import lombok.Getter;
import lombok.Setter;

/**
 * This Class is for an Answer of a Freetext-Questions.
 * It provides getter as well as setter.
 * @author Lukas Burkhardt
 * @version $I$
 */
@Getter
@Setter
public class AnsweredFreeTextQuestion extends AnsweredQuestion{
    private String answer;
}
