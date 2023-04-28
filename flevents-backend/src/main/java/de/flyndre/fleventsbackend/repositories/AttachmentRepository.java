package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment,String> {
}
