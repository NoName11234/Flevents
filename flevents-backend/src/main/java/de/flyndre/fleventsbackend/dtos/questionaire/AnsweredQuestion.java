package de.flyndre.fleventsbackend.dtos.questionaire;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

/**
 * This Class is the Answer for Questions.
 * It provides getter as well as setter.
 * @author Lukas Burkhardt
 * @version $I$
 */
@Getter
@Setter
public class AnsweredQuestion {
    private String uuid;
    @Nullable
    private String choice;
    @Nullable
    private String answer;
}
