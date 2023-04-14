package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization,String> {

        Organization findByName(String name);
}
