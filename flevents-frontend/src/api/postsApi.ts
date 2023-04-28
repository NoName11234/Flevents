import api from "@/api/api";
import {FleventsEvent} from "@/models/fleventsEvent";
import {EventRole} from "@/models/eventRole";
import {Post} from "@/models/post";

/**
 * Api endpoints to access and modify posts.
 * @author David Maier
 * @since Weekly Build 3
 */

class PostsApi {

  // CRUD

  /**
   * Creates a post.
   * @param post the post to be created
   * @param eventUuid the uuid of the event the post belongs to
   * @param attachments list of files to be appended to the post
   */
  create(post: Post, eventUuid: string, attachments: File[]) {
    let data = new FormData();
    attachments.forEach(v => data.append('attachments', v));
    data.append('post', new Blob([JSON.stringify(post)], {type: 'application/json'}));
    return api.post(`/events/${eventUuid}/posts`, data, {
      headers: { "Content-Type": undefined }
    });
  }

  /**
   * Retrieves a post.
   * @param uuid the uuid of the post
   * @param eventUuid the uuid of the post's event
   */
  get(uuid: string, eventUuid: string) {
    return api.get(`/events/${eventUuid}/posts/${uuid}`);
  }

  getOf(eventUuid: string) {
    return api.get(`/events/${eventUuid}/posts`);
  }

  /**
   * Modifies the post with the given uuid.
   * @param uuid the uuid of the post
   * @param eventUuid the uuid of the post's event
   * @param post a post object containing only the attributes to be modified
   * @param addedAttachments list of files to be appended to the post additionally to existing ones
   */
  edit(uuid: string, eventUuid: string, post: Post, addedAttachments: File[]) {
    let data = new FormData();
    addedAttachments.forEach(v => data.append('attachments', v));
    data.append('post', new Blob([JSON.stringify(post)], {type: 'application/json'}));
    return api.post(`/events/${eventUuid}/posts/${uuid}`, data, {
      headers: { "Content-Type": undefined }
    });
  }

  /**
   * Deletes a post.
   * @param uuid the uuid of the post
   * @param eventUuid the uuid of the post's event
   */
  delete(uuid: string, eventUuid: string) {
    return api.delete(`/events/${eventUuid}/posts/${uuid}`);
  }
}

export default new PostsApi();
