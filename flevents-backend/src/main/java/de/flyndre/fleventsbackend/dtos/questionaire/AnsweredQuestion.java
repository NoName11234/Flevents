package de.flyndre.fleventsbackend.dtos.questionaire;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
public class AnsweredQuestion {
    private String uuid;
    @Nullable
    private String choice;
    @Nullable
    private String answer;
}
