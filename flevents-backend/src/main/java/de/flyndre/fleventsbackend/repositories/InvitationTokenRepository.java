package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.InvitationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationTokenRepository extends JpaRepository<InvitationToken,String> {
}
