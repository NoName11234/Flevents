package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.Models.Post;
import de.flyndre.fleventsbackend.Models.PostComment;
import de.flyndre.fleventsbackend.repositories.PostRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import org.aspectj.weaver.AjAttribute;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getPosts(Event event){
        return postRepository.findByEvent_Uuid(event.getUuid());
    }
    public Post getPostById(String postId){
        Optional<Post> optional = postRepository.findById(postId);
        if(!optional.isPresent()){
            throw new NoSuchElementException("Could not found a Post with this id");
        }
        return optional.get();
    }
    public Post createPost(Post post){
        return postRepository.save(post);
    }

    public Post updatePost(String postId,Post post){
        Optional<Post> optional = postRepository.findById(postId);
        if(!optional.isPresent()){
            throw new NoSuchElementException("Could not found a Post with this id");
        }
        Post srcPost = optional.get();
        srcPost.merge(post);
        return postRepository.save(srcPost);
    }

    public Post createComment(Post post,PostComment comment){
        post.getComments().add(comment);
        return postRepository.save(post);
    }
}
