package de.flyndre.fleventsbackend.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.*;
/**
 * This Class is the Modelclass for Posts.
 * It provides getter as well as setter and a Merge-Method.
 * @implNote This Model is O/R-Mapped to a Database
 * @author Lukas Burkhardt
 * @version $I$
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String uuid;

    @ManyToOne
    private FleventsAccount author;
    private String title;
    @Lob
    private String content;

    @ManyToOne
    private Event event;

    private LocalDateTime creationDate;
    @OneToMany(mappedBy = "post")
    private List<PostComment> comments = new ArrayList<>();
    @OneToMany(mappedBy = "post")
    private List<Attachment> attachments = new ArrayList<>();

    public void addAttachment(Attachment attachment){
        this.attachments.add(attachment);
    }

    public void merge(Post post){
        if(post.getTitle()!=null){
            this.title= post.getTitle();
        }
        if(post.getContent()!=null){
            this.content = post.getContent();
        }
    }
}
