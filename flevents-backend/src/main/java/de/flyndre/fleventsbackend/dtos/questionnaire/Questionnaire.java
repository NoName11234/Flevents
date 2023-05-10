package de.flyndre.fleventsbackend.dtos.questionnaire;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class Questionnaire {
    private String uuid;
    private String eventId;
    private String title;
    private LocalDateTime creationDate;
    private LocalDateTime closingDate;
    private List<Question> questions;
}
