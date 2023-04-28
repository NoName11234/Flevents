package de.flyndre.fleventsbackend.dtos.questionnaire;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

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
