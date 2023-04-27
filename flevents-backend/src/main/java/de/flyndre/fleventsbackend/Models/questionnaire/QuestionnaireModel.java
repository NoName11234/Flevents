package de.flyndre.fleventsbackend.Models.questionnaire;

import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.dtos.questionnaire.Questionnaire;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionnaireModel {
    @Id @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String uuid;
    @ManyToOne
    private Event event;
    private String title;
    private Timestamp creationDate;
    private Timestamp closingDate;
    @OneToMany
    private List<QuestionModel> questions = new ArrayList<>();
    @OneToMany
    private List<AnsweredQuestionnaireModel> answeredQuestionnaireModels = new ArrayList<>();

    public void merge(QuestionnaireModel questionnaireModel){
        if(questionnaireModel.title != null){
            this.title = questionnaireModel.getTitle();
        }
        if(questionnaireModel.creationDate != null){
            this.creationDate = questionnaireModel.getCreationDate();
        }
        if(questionnaireModel.closingDate != null){
            this.closingDate = questionnaireModel.getClosingDate();
        }
    }
}