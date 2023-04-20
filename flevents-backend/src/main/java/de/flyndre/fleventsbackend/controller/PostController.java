package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.Post;
import de.flyndre.fleventsbackend.Models.PostComment;
import de.flyndre.fleventsbackend.controllerServices.PostControllerService;
import de.flyndre.fleventsbackend.dtos.PostInformation;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * Author: Lukas Burkhardt
 * Version:
 * This Class is the Controller for the REST-API path "/api/events".
 * It provides an interface regarding posts.
 */

@RestController
@CrossOrigin
@RequestMapping("/api/events/{eventId}/posts")
public class PostController {
    private  final PostControllerService postControllerService;
    private final ModelMapper mapper;

    public PostController(PostControllerService postControllerService, ModelMapper mapper) {
        this.postControllerService = postControllerService;
        this.mapper = mapper;
    }

    /**
     * Returns a list with all posts of the specified event.
     * @param eventId the id of the event to get the posts from
     * @return ResponseEntity a list of the posts and the http status code
     */
    @GetMapping
    public ResponseEntity getPosts(@PathVariable String eventId){
        return new ResponseEntity<>(postControllerService.getPosts(eventId).stream().map(post -> mapper.map(post, PostInformation.class)).collect(Collectors.toList()), HttpStatus.OK);
    }

    /**
     * Returns a single, specified post from the specified event.
     * @param eventId the id of the event to get the post from
     * @param postId the id of the post
     * @return ReponseEntity with the post and the http status code
     */
    @GetMapping("/{postId}")
    public ResponseEntity getPost(@PathVariable String eventId, @PathVariable String postId){
        PostInformation information = mapper.map(postControllerService.getPost(postId),PostInformation.class);
        return new ResponseEntity(information,HttpStatus.OK);
    }

    /**
     * Creates a post in the specified id by the specified account.
     * @param eventId the id of the event to create the post in
     * @param accountId the id of the account which is the author of the post
     * @param post the post to be created
     * @return ResponseEntity with the created post and the http status code
     */
    @PostMapping
    public ResponseEntity createPost(@PathVariable String eventId,@RequestParam String accountId,@RequestBody Post post){
        return new ResponseEntity(mapper.map(postControllerService.createPost(eventId,accountId,post), PostInformation.class),HttpStatus.OK);
    }

    /**
     * Creates a comment under the specified post by the specified account in the specified event.
     * @param eventId the id of the event with the post to create the comment under
     * @param postId the id of the post in the event to create the comment under
     * @param accountId the id of the account which is the author of the comment
     * @param comment the comment to be created
     * @return ReponseEntity with the commented post and the http status code
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity createComment(@PathVariable String eventId, @PathVariable String postId, @RequestParam String accountId,@RequestBody PostComment comment){
        return new ResponseEntity(mapper.map(postControllerService.createComment(postId,eventId,accountId,comment), PostInformation.class),HttpStatus.OK);
    }
}
