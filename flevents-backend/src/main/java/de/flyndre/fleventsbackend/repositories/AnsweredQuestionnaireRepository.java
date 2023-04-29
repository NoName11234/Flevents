package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.Models.questionnaire.AnsweredQuestionnaireModel;
import de.flyndre.fleventsbackend.Models.questionnaire.QuestionnaireModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnsweredQuestionnaireRepository extends JpaRepository<AnsweredQuestionnaireModel,String> {

    public AnsweredQuestionnaireModel findByUserAndQuestionnaireModel(FleventsAccount user, QuestionnaireModel questionnaire);
}
