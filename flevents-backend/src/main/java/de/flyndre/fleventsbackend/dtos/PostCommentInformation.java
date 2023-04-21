package de.flyndre.fleventsbackend.dtos;

import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.Models.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

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
