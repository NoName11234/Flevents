package de.flyndre.fleventsbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostPreview {
    private String uuid;
    private AccountPreview author;
    private String title;
    private String content;
    private LocalDateTime creationDate;
}
