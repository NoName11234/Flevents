package de.flyndre.fleventsbackend.dtos.questionaire;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class SingleChoiceQuestion extends Question{
    private List<String> choices;
}
