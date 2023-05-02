package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.questionnaire.AnsweredQuestionModel;
import de.flyndre.fleventsbackend.Models.questionnaire.AnsweredQuestionnaireModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnsweredQuestionRepository extends JpaRepository<AnsweredQuestionModel,String> {
    Optional<AnsweredQuestionModel> findAllByUuid(String uuid);
}
