package de.flyndre.fleventsbackend.Models.questionnaire;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
    public enum QuestionType {FreeTextQuestion, SingleChoiceQuestion}
    @Nullable @OneToOne(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private ChoiceModel choiceModel;
    @Nullable
    private String answer;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private AnsweredQuestionnaireModel answeredQuestionnaireModel;
}
