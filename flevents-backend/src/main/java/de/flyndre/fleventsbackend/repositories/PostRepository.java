package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface PostRepository extends JpaRepository<Post,String> {
    public List<Post> findByEvent_Uuid(String eventUuid);
}
