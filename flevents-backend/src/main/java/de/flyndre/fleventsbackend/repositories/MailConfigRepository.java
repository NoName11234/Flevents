package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.MailConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailConfigRepository extends JpaRepository<MailConfig,String> {
}
