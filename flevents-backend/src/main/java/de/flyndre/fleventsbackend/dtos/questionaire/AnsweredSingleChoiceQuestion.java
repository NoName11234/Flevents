package de.flyndre.fleventsbackend.dtos.questionaire;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnsweredSingleChoiceQuestion extends AnsweredQuestion{
    private String choice;
}
