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

    @GetMapping
    public ResponseEntity getPosts(@PathVariable String eventId){
        return new ResponseEntity<>(postControllerService.getPosts(eventId).stream().map(post -> mapper.map(post, PostInformation.class)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity getPost(@PathVariable String eventId, @PathVariable String postId){
        PostInformation information = mapper.map(postControllerService.getPost(postId),PostInformation.class);
        return new ResponseEntity(information,HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity createPost(@PathVariable String eventId,@RequestParam String accountId,@RequestBody Post post){
        return new ResponseEntity(mapper.map(postControllerService.createPost(eventId,accountId,post), PostInformation.class),HttpStatus.OK);
    }
    @PostMapping("/{postId}/comments")
    public ResponseEntity createComment(@PathVariable String eventId, @PathVariable String postId, @RequestParam String accountId,@RequestBody PostComment comment){
        return new ResponseEntity(mapper.map(postControllerService.createComment(postId,eventId,accountId,comment), PostInformation.class),HttpStatus.OK);
    }
}
