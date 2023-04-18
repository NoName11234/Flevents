package de.flyndre.fleventsbackend.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.jbosslog.JBossLog;
import org.hibernate.SessionEventListener;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.*;

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

    private Timestamp creationDate;
    @OneToMany(mappedBy = "post")
    private List<PostComment> comments = new ArrayList<>();

    public void merge(Post post){
        if(post.getTitle()!=null){
            this.title= post.getTitle();
        }
        if(post.getContent()!=null){
            this.content = post.getContent();
        }
    }
}
