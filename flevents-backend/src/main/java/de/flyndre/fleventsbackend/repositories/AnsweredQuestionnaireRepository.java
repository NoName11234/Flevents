package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.questionnaire.AnsweredQuestionnaire;
import de.flyndre.fleventsbackend.Models.questionnaire.QuestionnaireModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnsweredQuestionnaireRepository extends JpaRepository<AnsweredQuestionnaire,String> {
    
}
