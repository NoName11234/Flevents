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

import java.util.ArrayList;
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
    public enum QuestionType {FreeTextQuestion, SingleChoiceQuestion}
    @ManyToOne(cascade = CascadeType.PERSIST)
    private QuestionnaireModel questionnaire;
    @Nullable @OneToMany(cascade = CascadeType.PERSIST)
    private List<ChoiceModel> choiceModels = new ArrayList<>();
}
