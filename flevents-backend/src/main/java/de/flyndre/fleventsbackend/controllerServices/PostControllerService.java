package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.Models.Post;
import de.flyndre.fleventsbackend.Models.PostComment;
import de.flyndre.fleventsbackend.services.EventService;
import de.flyndre.fleventsbackend.services.FleventsAccountService;
import de.flyndre.fleventsbackend.services.PostService;
import org.springframework.stereotype.Service;

import javax.xml.stream.events.Comment;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostControllerService {
    private final EventService eventService;
    private final PostService postService;
    private final FleventsAccountService accountService;

    public PostControllerService(EventService eventService, PostService postService, FleventsAccountService accountService) {
        this.eventService = eventService;
        this.postService = postService;
        this.accountService = accountService;
    }

    public List<Post> getPosts(String eventId){
        return postService.getPosts(eventService.getEventById(eventId));
    }

    public Post getPost(String postId){
        return postService.getPostById(postId);
    }

    public Post createPost(String eventId, String accountId, Post post){
        post.setEvent(eventService.getEventById(eventId));
        post.setAuthor(accountService.getAccountById(accountId));
        post.setCreationDate(LocalDateTime.now());
        post.setUuid(null);
        return postService.createPost(post);

    }

    public Post updatePost(String postId, Post post){
        return postService.updatePost(postId,post);
    }

    public Post createComment(String postId,String eventId, String accountId, PostComment comment){
        comment.setPost(postService.getPostById(postId));
        comment.setAuthor(accountService.getAccountById(accountId));
        comment.setCreationDate(LocalDateTime.now());
        comment.setUuid(null);
        return postService.createComment(postService.getPostById(postId),comment);
    }
}
