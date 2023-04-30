package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.questionnaire.QuestionnaireModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionnaireRepository extends JpaRepository<QuestionnaireModel,String> {
    public List<QuestionnaireModel> findByEvent_Uuid(String uuid);

    Optional<QuestionnaireModel> findAllByUuid(String uuid);
}
