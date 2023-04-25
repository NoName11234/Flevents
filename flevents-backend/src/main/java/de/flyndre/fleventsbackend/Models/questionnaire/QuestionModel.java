package de.flyndre.fleventsbackend.Models.questionnaire;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.Nullable;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionModel {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String uuid;
    private String question;
    private enum QuestionType {FreeTextQuestion, SingleChoiceQuestion}
    @ManyToOne
    private QuestionnaireModel questionnaire;
    @Nullable @OneToMany
    private List<Choice> choices;
    private String type;
}
