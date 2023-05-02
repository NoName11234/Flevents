package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.Attachment;
import de.flyndre.fleventsbackend.Models.Event;
import de.flyndre.fleventsbackend.Models.Post;
import de.flyndre.fleventsbackend.Models.PostComment;
import de.flyndre.fleventsbackend.repositories.PostCommentRepository;
import de.flyndre.fleventsbackend.repositories.PostRepository;

import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * This Service provides logic and usage for the Post repository.
 * It provides methods for manipulating the data of these repositories.
 * @author Lukas Burkhardt
 * @version $I$
 */
@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostCommentRepository commentRepository;

    public PostService(PostRepository postRepository, PostCommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Get all posts from the given event.
     * @param event the event to get the posts from
     * @return list of all posts
     */
    public List<Post> getPosts(Event event){
        return postRepository.findByEvent_Uuid(event.getUuid());
    }

    /**
     * Returns the post with the given id. Throws an Exception if there is no  post with the id.
     * @param postId the id of the post to be returned
     * @return the post
     */
    public Post getPostById(String postId){
        Optional<Post> optional = postRepository.findById(postId);
        if(!optional.isPresent()){
            throw new NoSuchElementException("Could not found a Post with this id. Does it exist? ID: "+postId);
        }
        return optional.get();
    }

    /**
     * Creates the given post in the database.
     * @param post the post to be created
     * @return the created post
     */
    public Post createPost(Post post){
        return postRepository.save(post);
    }

    /**
     * Overwrites the specified post with the given post object. Throws an Exception if there is no post with the specified id.
     *
     * @param postId      the id of the post to overwrite
     * @param post        the new post
     * @param attachments
     * @return the overwritten post
     */
    public Post updatePost(String postId, Post post, List<Attachment> attachments){
        Post srcPost = getPostById(postId);
        if(post!=null){
            srcPost.merge(post);
        }
        if(attachments!=null){
            attachments.forEach(attachment -> addAttachment(srcPost,attachment));
        }
        return postRepository.save(srcPost);
    }

    /**
     * Creates a comment in the specified post.
     * @param comment the comment to be created
     * @return the post in which the comment was created
     */
    public PostComment createComment(PostComment comment){
        return commentRepository.save(comment);
    }

    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    /**
     * Add an Attachemnt to an existing post and save it.
     * @param post post to add attachment
     * @param attachment attachment to add
     * @return the manipulated post
     */
    public Post addAttachment(Post post, Attachment attachment) {
        post.addAttachment(attachment);
        return postRepository.save(post);
    }

    /**
     * Get a comment by its id.
     * @param commentId the id of the wanted comment
     * @return the Comment if its exist
     * @throws NoSuchElementException if there is no comment to the given id.
     */
    public PostComment getComment(String commentId) {
        Optional<PostComment> optional = commentRepository.findById(commentId);
        if(optional.isEmpty()){
            throw new NoSuchElementException("The comment with the given id was not found. Id: "+commentId);
        }
        return optional.get();
    }

    /**
     * Delete a comment out of the database.
     * @param comment the comment to delete.
     */
    public void deleteComment(PostComment comment) {
        commentRepository.delete(comment);
    }

    /**
     * Deletes a post from the database
     * @param post the post to delete
     */
    public void deletePost(Post post) {
        post.getComments().forEach(comment -> deleteComment(comment));
        postRepository.delete(post);
    }
}
