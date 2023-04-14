package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.EventRegistration;
import de.flyndre.fleventsbackend.Models.EventRole;
import de.flyndre.fleventsbackend.Models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration,String> {
    public List<EventRegistration> findByAccount_UuidAndRole(String accountUuid, EventRole role);
    public Optional<EventRegistration> findByAccount_UuidAndEvent_UuidAndRole(String accountUuid,String eventUuid,EventRole role);
    public List<EventRegistration> findByEvent_UuidAndRole(String eventUuid,EventRole role);
}
