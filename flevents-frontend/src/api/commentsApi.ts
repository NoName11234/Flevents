import api from "@/api/api";
import {FleventsEvent} from "@/models/fleventsEvent";
import {EventRole} from "@/models/eventRole";
import {Post} from "@/models/post";
import {Comment} from "@/models/comment";

/**
 * Api endpoints to access and modify comments.
 * @author David Maier
 * @since Weekly Build 3
 */

class CommentsApi {

  // CRUD

  /**
   * Creates a comment.
   * @param comment the comment to be created
   * @param postUuid the uuid of the post the comment should belong to
   * @param eventUuid the uuid of the post's event
   */
  create(comment: Comment, postUuid: string, eventUuid: string) {
    return api.post(`/events/${eventUuid}/posts/${postUuid}/comments`, comment);
  }

  /**
   * Deletes a comment.
   * @param uuid the uuid of the comment
   * @param postUuid the uuid of the post the comment should belong to
   * @param eventUuid the uuid of the post's event
   */
  delete(uuid: string, postUuid: string, eventUuid: string) {
    return api.delete(`/events/${eventUuid}/posts/${postUuid}/comments/${uuid}`);
  }
}

export default new CommentsApi();
