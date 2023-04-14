package de.flyndre.fleventsbackend.dtos.questionaire;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnsweredFreeTextQuestion extends AnsweredQuestion{
    private String answer;
}
