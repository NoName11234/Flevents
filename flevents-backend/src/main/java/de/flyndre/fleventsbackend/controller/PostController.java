package de.flyndre.fleventsbackend.controller;

import de.flyndre.fleventsbackend.Models.Attachment;
import de.flyndre.fleventsbackend.Models.EventRole;
import de.flyndre.fleventsbackend.Models.Post;
import de.flyndre.fleventsbackend.Models.PostComment;
import de.flyndre.fleventsbackend.controllerServices.PostControllerService;
import de.flyndre.fleventsbackend.dtos.PostInformation;
import de.flyndre.fleventsbackend.security.services.UserDetailsImpl;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
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
    private final Logger logger = LoggerFactory.getLogger(PostController.class);
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
        try{
            return new ResponseEntity<>(postControllerService.getPosts(eventId).stream()
                    .map(post -> mapper.map(post, PostInformation.class)).collect(Collectors.toList()), HttpStatus.OK);
        }catch (Exception e){
            logger.error("Internal Error",e);
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Returns a single, specified post from the specified event.
     * Allows access for guest and above.
     * @param eventId the id of the event to get the post from
     * @param postId the id of the post
     * @param auth the Authentication generated out of a barer token.
     * @return ResponseEntity with the post and the http status code
     */
    @GetMapping("/{postId}")
    public ResponseEntity getPost(@PathVariable String eventId, @PathVariable String postId,Authentication auth){
        if(!postControllerService.getGranted(auth,eventId, Arrays.asList(EventRole.organizer,EventRole.tutor,EventRole.attendee,EventRole.guest))){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        try{
            PostInformation information = mapper.map(postControllerService.getPost(postId),PostInformation.class);
            return new ResponseEntity(information,HttpStatus.OK);
        }catch (Exception e){
            logger.error("Internal Error",e);
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a post in the specified id by the specified account.
     * Allows access for tutor and above.
     * @param eventId the id of the event to create the post in
     * @param post the post to be created
     * @param auth the Authentication generated out of a barer token.
     * @param attachments some files to add as an attachment.
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
        try{
            UserDetailsImpl authUser = (UserDetailsImpl) auth.getPrincipal();
            return new ResponseEntity(
                    mapper.map(postControllerService.createPost(eventId,authUser.getId(),post,attachments), PostInformation.class),
                    HttpStatus.OK);
        }catch (Exception e){
            logger.error("Internal Error",e);
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an existing post.
     * @param eventId the event of the post
     * @param postId the id of the post to update
     * @param post the post with all parameters set that should be updated
     * @param attachments attachments to add to the post
     * @param auth the Authentication generated out of a barer token.
     * @return the updated post or an error if something went wrong.
     */
    @PostMapping("/{postId}")
    public ResponseEntity updatePost(@PathVariable String eventId,
                                     @PathVariable String postId,
                                     @Nullable @RequestPart Post post,
                                     @Nullable @RequestPart List<MultipartFile> attachments,
                                     Authentication auth){
        if(!postControllerService.getGranted(auth,eventId, Arrays.asList(EventRole.organizer,EventRole.tutor))){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        try{
            return new ResponseEntity(mapper.map(
                    postControllerService.updatePost(postId,post,attachments),
                    PostInformation.class
            ),HttpStatus.OK);
        }catch (Exception e){
            logger.error("Internal Error",e);
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * Deleting an Post and all comments of it.
     * @param eventId the id of event of the post
     * @param postId the id of the post to be deleted
     * @param auth the Authentication generated out of a barer token.
     * @return ok or an error if something went wrong
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity deletePost(@PathVariable String eventId, @PathVariable String postId,Authentication auth){
        if(!postControllerService.getGranted(auth,eventId, Arrays.asList(EventRole.organizer,EventRole.tutor))){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        try{
            postControllerService.deletePost(postId);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            logger.error("Internal Error",e);
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a comment under the specified post by the specified account in the specified event.
     * Allows access for tutor and above.
     * @param eventId the id of the event with the post to create the comment under
     * @param postId the id of the post in the event to create the comment under
     * @param comment the comment to be created
     * @param auth the Authentication generated out of a barer token.
     * @return ResponseEntity with the commented post and the http status code
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity createComment(@PathVariable String eventId, @PathVariable String postId,@RequestBody PostComment comment,Authentication auth){
        if(!postControllerService.getGranted(auth,eventId, Arrays.asList(EventRole.organizer,EventRole.tutor,EventRole.attendee,EventRole.guest))){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        try{
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            return new ResponseEntity(
                    mapper.map(postControllerService.createComment(postId,eventId,userDetails.getId(),comment), PostInformation.class),
                    HttpStatus.OK);
        }catch (Exception e){
            logger.error("Internal Error",e);
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a comment and all its associations.
     * @param eventId the event of the post of the comment
     * @param postId the post of the comment
     * @param commentId the id of the comment to delete
     * @param authentication the Authentication generated out of a barer token.
     * @return Ok or an error if something went wrong.
     */
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity deleteComment(@PathVariable String eventId,@PathVariable String postId, @PathVariable String commentId,Authentication authentication){
        try{
            postControllerService.deleteComment(commentId);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            logger.error("Internal Error",e);
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Returns the file that is represented by the attachment id.
     * @param eventId id of the event of the post to which the attachment is binded
     * @param attachmentId the id of the attachment
     * @return the raw file or an error if something went wrong.
     */
    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity getAttachment(@PathVariable String eventId,@PathVariable String attachmentId){
        try{
            Attachment attachment = postControllerService.getAttachment(attachmentId);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+attachment.getFilename()+"\"").body(attachment.getData());
        }catch (Exception e){
            logger.error("Internal Error",e);
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Adds a MultipartFile as an attachment to an existing post.
     * @param eventId the id of the event of the post
     * @param postId the id of the post to add to
     * @param attachment the MultipartFile to add
     * @param auth the Authentication generated out of a barer token.
     * @return the manipulated post or an error if something went wrong
     */
    @PostMapping("/{postId}/attachments")
    public ResponseEntity addAttachment(@PathVariable String eventId,@PathVariable String postId,@RequestBody MultipartFile attachment,Authentication auth){
        if(!postControllerService.getGranted(auth,eventId,Arrays.asList(EventRole.organizer,EventRole.tutor))){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        try{
            return new ResponseEntity(mapper.map(postControllerService.addAttachment(postId,attachment), PostInformation.class),HttpStatus.OK);
        }catch (Exception e){
            logger.error("Internal Error",e);
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete an Attachment form a Post
     * @param eventId the id of the event of the post
     * @param attachmentId the id of the attachment to delete
     * @return ok if successful or en error of something went wrong
     */
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity deleteAttachment(@PathVariable String eventId, @PathVariable String attachmentId,Authentication auth){
        if(!postControllerService.getGranted(auth,eventId,Arrays.asList(EventRole.organizer,EventRole.tutor))){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        try{
            postControllerService.deleteAttachment(attachmentId);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            logger.error("Internal Error",e);
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
