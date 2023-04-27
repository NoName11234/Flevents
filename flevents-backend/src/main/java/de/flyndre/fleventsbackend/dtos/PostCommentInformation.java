package de.flyndre.fleventsbackend.dtos;

import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.Models.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * This Class is the Data Transfer Object for the Information of Comments of Posts.
 * It provides getter as well as setter.
 * @implNote This DTO should only be returned in the Controller
 * @author Lukas Burkhardt
 * @version $I$
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentInformation {
    private String uuid;
    private FleventsAccount author;
    private Post post;
    private LocalDateTime creationDate;
    private String content;
}
