package de.flyndre.fleventsbackend.dtos.questionaire;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * This Class is a common Questions.
 * It provides getter as well as setter.
 * @implNote This Class should not be used and should only be extended of
 * @author Lukas Burkhardt
 * @version $I$
 */
@Getter
@Setter
public class Question {
    private String uuid;
    private String question;
    @Nullable
    private List<String> choices;
    private String type;
}
