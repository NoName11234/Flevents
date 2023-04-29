package de.flyndre.fleventsbackend.Models.questionnaire;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.Nullable;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnsweredQuestionModel {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String uuid;
    private enum QuestionType {FreeTextQuestion, SingleChoiceQuestion}
    @Nullable @OneToOne(cascade = CascadeType.ALL)
    private ChoiceModel choiceModel;
    @Nullable
    private String answer;
    @ManyToOne(cascade = CascadeType.ALL)
    private AnsweredQuestionnaireModel answeredQuestionnaireModel;
}
