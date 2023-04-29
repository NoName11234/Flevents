package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.questionnaire.AnsweredQuestionnaireModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnsweredQuestionRepository extends JpaRepository<AnsweredQuestionnaireModel,String> {
}
