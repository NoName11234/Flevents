package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.questionnaire.ChoiceModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChoiceRepository extends JpaRepository<ChoiceModel,String> {

    Optional<ChoiceModel> findAllByUuid(String uuid);
}
