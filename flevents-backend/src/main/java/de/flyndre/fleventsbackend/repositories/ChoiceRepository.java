package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.questionnaire.ChoiceModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<ChoiceModel,String> {
}
