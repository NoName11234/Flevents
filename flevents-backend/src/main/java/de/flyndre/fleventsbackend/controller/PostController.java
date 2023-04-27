package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.Models.EventRole;
import de.flyndre.fleventsbackend.Models.Post;
import de.flyndre.fleventsbackend.Models.PostComment;
import de.flyndre.fleventsbackend.controllerServices.PostControllerService;
import de.flyndre.fleventsbackend.dtos.PostInformation;
import de.flyndre.fleventsbackend.security.services.UserDetailsImpl;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This Class is the Controller for the REST-API path "/api/events".
 * It provides an interface regarding posts.
 * @author Lukas Burkhardt
 * @version $I$
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
     * Allows access for guest and above.
     * @param eventId the id of the event to get the posts from
     * @param auth the Authentication generated out of a barer token.
     * @return ResponseEntity a list of the posts and the http status code
     */
    @GetMapping
    public ResponseEntity getPosts(@PathVariable String eventId, Authentication auth){
        if(!postControllerService.getGranted(auth,eventId, Arrays.asList(EventRole.organizer,EventRole.tutor,EventRole.attendee,EventRole.guest))){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(postControllerService.getPosts(eventId).stream()
                .map(post -> mapper.map(post, PostInformation.class)).collect(Collectors.toList()), HttpStatus.OK);
    }

    /**
     * Returns a single, specified post from the specified event.
     * Allows access for guest and above.
     * @param eventId the id of the event to get the post from
     * @param postId the id of the post
     * @param auth the Authentication generated out of a barer token.
     * @return ReponseEntity with the post and the http status code
     */
    @GetMapping("/{postId}")
    public ResponseEntity getPost(@PathVariable String eventId, @PathVariable String postId,Authentication auth){
        if(!postControllerService.getGranted(auth,eventId, Arrays.asList(EventRole.organizer,EventRole.tutor,EventRole.attendee,EventRole.guest))){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        PostInformation information = mapper.map(postControllerService.getPost(postId),PostInformation.class);
        return new ResponseEntity(information,HttpStatus.OK);
    }

    /**
     * Creates a post in the specified id by the specified account.
     * Allows access for tutor and above.
     * @param eventId the id of the event to create the post in
     * @param post the post to be created
     * @param auth the Authentication generated out of a barer token.
     * @return ResponseEntity with the created post and the http status code
     */
    @PostMapping
    public ResponseEntity createPost(
            @PathVariable String eventId,
            @RequestPart Post post,
            @Nullable @RequestPart List<MultipartFile> attachments,
            Authentication auth
    ){
        if(!postControllerService.getGranted(auth,eventId, Arrays.asList(EventRole.organizer,EventRole.tutor))){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        // TODO: store attachments
        if (attachments != null) {
            attachments.forEach(a -> System.out.println(a.getOriginalFilename()));
        }
        UserDetailsImpl authUser = (UserDetailsImpl) auth.getPrincipal();
        return new ResponseEntity(mapper.map(postControllerService.createPost(eventId,authUser.getId(),post), PostInformation.class),HttpStatus.OK);
    }

    /**
     * Creates a comment under the specified post by the specified account in the specified event.
     * Allows access for tutor and above.
     * @param eventId the id of the event with the post to create the comment under
     * @param postId the id of the post in the event to create the comment under
     * @param accountId the id of the account which is the author of the comment
     * @param comment the comment to be created
     * @param auth the Authentication generated out of a barer token.
     * @return ResponseEntity with the commented post and the http status code
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity createComment(@PathVariable String eventId, @PathVariable String postId, @RequestParam String accountId,@RequestBody PostComment comment,Authentication auth){
        if(!postControllerService.getGranted(auth,eventId, Arrays.asList(EventRole.organizer,EventRole.tutor,EventRole.attendee,EventRole.guest))){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(mapper.map(postControllerService.createComment(postId,eventId,accountId,comment), PostInformation.class),HttpStatus.OK);
    }
}
