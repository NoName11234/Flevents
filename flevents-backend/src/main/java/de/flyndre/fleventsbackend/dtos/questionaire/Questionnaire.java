package de.flyndre.fleventsbackend.dtos.questionaire;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
/**
 * This Class is a model of a Questionnaire.
 * It provides getter as well as setter.
 * @author Lukas Burkhardt
 * @version $I$
 */
@Getter
@Setter
public class Questionnaire {
    private String uuid;
    private String eventId;
    private String title;
    private Timestamp creationDate;
    private Timestamp closingDate;
    private List<Question> questions;
}
