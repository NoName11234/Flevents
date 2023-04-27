package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment,String> {
}
