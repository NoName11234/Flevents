package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.questionnaire.QuestionModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<QuestionModel,String> {

    Optional<QuestionModel> findAllByUuid(String uuid);
}
