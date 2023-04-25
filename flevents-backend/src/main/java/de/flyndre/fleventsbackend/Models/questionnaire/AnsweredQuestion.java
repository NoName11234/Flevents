package de.flyndre.fleventsbackend.Models.questionnaire;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
public class AnsweredQuestion {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String uuid;
    @Nullable
    private String choice;
    @Nullable
    private String answer;
}
