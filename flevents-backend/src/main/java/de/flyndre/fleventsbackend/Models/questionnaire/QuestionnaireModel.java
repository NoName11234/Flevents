package de.flyndre.fleventsbackend.Models.questionnaire;

import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.dtos.questionnaire.Questionnaire;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
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
    @ManyToOne(cascade = CascadeType.REFRESH)

    private Event event;
    private String title;
    private LocalDateTime creationDate;
    private LocalDateTime closingDate;
    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true, mappedBy = "questionnaire")
    private List<QuestionModel> questions = new ArrayList<>();
    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true, mappedBy = "questionnaireModel")
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
