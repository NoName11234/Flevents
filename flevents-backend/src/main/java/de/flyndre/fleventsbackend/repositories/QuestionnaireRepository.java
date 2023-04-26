package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.questionnaire.QuestionnaireModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionnaireRepository extends JpaRepository<QuestionnaireModel,String> {
    public List<QuestionnaireModel> findByEvent_Uuid(String eventUuid);
}
