package de.flyndre.fleventsbackend.dtos;

import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.Models.PostComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostInformation {
    private String uuid;
    private AccountPreview author;
    private String title;
    private String content;
    private Timestamp creationDate;
    private EventPreview event;
    private List<PostComment> comments;
}
