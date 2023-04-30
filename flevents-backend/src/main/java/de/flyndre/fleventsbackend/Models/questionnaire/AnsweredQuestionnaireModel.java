package de.flyndre.fleventsbackend.Models.questionnaire;

import de.flyndre.fleventsbackend.Models.FleventsAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnsweredQuestionnaireModel {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String uuid;
    @ManyToOne(cascade = CascadeType.ALL)
    private FleventsAccount user;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnsweredQuestionModel> answers = new ArrayList<>();
    @ManyToOne(cascade = CascadeType.ALL)
    private QuestionnaireModel questionnaireModel;
}
