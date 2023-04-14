package de.flyndre.fleventsbackend.dtos.questionaire;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
@Setter
public class Question {
    private String uuid;
    private String question;
    @Nullable
    private List<String> choices;
    private String type;
}
