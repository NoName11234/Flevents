package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.services.*;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This Class is the service for the PostController class.
 * It provides methods regarding posts. The methods of the PostController are mapped on them.
 * @author Lukas Burkhardt
 * @version $I$
 */

@Service
public class PostControllerService {
    private final EventService eventService;
    private final PostService postService;
    private final FleventsAccountService accountService;
    private final AuthService authService;
    private final AttachmentService attachmentService;

    public PostControllerService(EventService eventService, PostService postService, FleventsAccountService accountService, AuthService authService, AttachmentService attachmentService) {
        this.eventService = eventService;
        this.postService = postService;
        this.accountService = accountService;
        this.authService = authService;
        this.attachmentService = attachmentService;
    }

    /**
     * Returns a list with all posts of the specified event.
     * @param eventId the id of the event to get the posts from
     * @return List<Post> with the posts of the event
     */
    public List<Post> getPosts(String eventId){
        return postService.getPosts(eventService.getEventById(eventId));
    }


    /**
     * Returns a single, specified post from the specified event.
     * @param postId the id of the post
     * @return the specified post
     */
    public Post getPost(String postId){
        return postService.getPostById(postId);
    }


    /**
     * Creates a post in the specified id by the specified account.
     *
     * @param eventId     the id of the event to create the post in
     * @param accountId   the id of the account which is the author of the post
     * @param post        the post to be created
     * @param attachments files to add as attachment.
     * @return the created post
     */
    public Post createPost(String eventId, String accountId, Post post,@Nullable List<MultipartFile> attachments){
        post.setEvent(eventService.getEventById(eventId));
        post.setAuthor(accountService.getAccountById(accountId));
        post.setCreationDate(LocalDateTime.now());
        post.setUuid(null);
        post = postService.createPost(post);
        if(attachments!=null) {
            Post finalPost = post;
            attachments.forEach(file -> {
                try {
                    Attachment attachment = attachmentService.createAttachment(file,finalPost);
                    finalPost.getAttachments().add(attachment);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            post = postService.savePost(finalPost);
        }
        return post;
    }

    /**
     * Overwrites the specified post with the given post.
     *
     * @param postId      the id of the post to be overwritten
     * @param post        the post to overwrite the old one with
     * @param attachments attachments to add to the post
     * @return the updated post
     */
    public Post updatePost(String postId, @Nullable Post post,@Nullable List<MultipartFile> attachments){
        if(attachments==null){
            attachments = new ArrayList<>();
        }
        List<Attachment> attachmentList = attachments.stream().map(file -> {
            try {
                return attachmentService.createAttachment(file,getPost(postId));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        return postService.updatePost(postId,post,attachmentList);
    }

    /**
     * Creates a comment under the specified post by the specified account in the specified event.
     * @param eventId the id of the event with the post to create the comment under
     * @param postId the id of the post in the event to create the comment under
     * @param accountId the id of the account which is the author of the comment
     * @param comment the comment to be created
     * @return the post with the new comment
     */
    public Post createComment(String postId,String eventId, String accountId, PostComment comment){
        comment.setPost(postService.getPostById(postId));
        comment.setAuthor(accountService.getAccountById(accountId));
        comment.setCreationDate(LocalDateTime.now());
        comment.setUuid(null);
        comment = postService.createComment(comment);
        return comment.getPost();
    }
    /**
     * Validate if the given Authentication matches to the given roles for the given event id.
     * @param auth the Authentication to validate.
     * @param eventId the id of the event in which context the validation should be done.
     * @param roles the event roles that should match.
     * @return true if the given parameters match, false if not.
     */
    public boolean getGranted(Authentication auth, String eventId, List<Role> roles){
        return authService.validateRights(auth, roles, eventId)||
                authService.validateRights(auth,Arrays.asList(OrganizationRole.admin),eventId);
    }

    /**
     * Decides if a user is the author of a comment an allowed to edit it.
     * @param auth the Authentication to validate.
     * @param commentId the id of the comment to check for.
     * @return true if user is author otherwise false.
     */
    public boolean getCommentGranted(Authentication auth, String commentId) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Not Implemented");
    }

    /**
     * Get attachment identified by an id
     * @param attachmentId the id of the attachment
     * @return the attachment or an error if something went wrong.
     */
    public Attachment getAttachment(String attachmentId) {
        return attachmentService.getAttachment(attachmentId);
    }

    /**
     * Add an MultipartFile to an existing post.
     * @param postId the id of the post to add to
     * @param attachment the MultipartFile to add
     * @return the manipulated Post
     * @throws IOException if the MultipartFile isn't present anymore.
     */
    public Post addAttachment(String postId, MultipartFile attachment) throws IOException {
        return postService.addAttachment(getPost(postId),attachmentService.createAttachment(attachment,postService.getPostById(postId)));
    }

    /**
     * Deletes the attachment anthe association to the post it belongs to.
     * @param attachmentId the id of the attachment to delete.
     */
    public void deleteAttachment(String attachmentId) {
        attachmentService.deleteAttachment(attachmentService.getAttachment(attachmentId));
    }

    /**
     * Delete a comment and it's
     * @param commentId the id of the comment to delete
     */
    public void deleteComment(String commentId) {
        postService.deleteComment(postService.getComment(commentId));
    }

    /**
     * Delete a post with all its associations
     * @param postId the id of the post to delete
     */
    public void deletePost(String postId) {
        Post post = getPost(postId);
        post.getAttachments().forEach(attachment -> attachmentService.deleteAttachment(attachment));
        postService.deletePost(post);
    }
}
