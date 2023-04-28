import api from "@/api/api";
import {Post} from "@/models/post";

/**
 * Api endpoints to access and modify attachments.
 * @author David Maier
 * @since Weekly Build 3
 */

class AttachmentsApi {

  // CRUD

  /**
   * Adds an attachment.
   * @param attachment the attachment to be added
   * @param postUuid the uuid of the post the attachment should be added to
   * @param eventUuid the uuid of the event the post belongs to
   */
  create(attachment: File, postUuid: string, eventUuid: string) {
    let data = new FormData();
    data.append('attachment', attachment);
    return api.post(`/events/${eventUuid}/posts/${postUuid}/attachments`, data, {
      headers: { "Content-Type": undefined }
    });
  }

  /**
   * Retrieves an attachment.
   * @param uuid the uuid of the attachment
   * @param eventUuid the uuid of the attachment's post's event
   */
  get(uuid: string, eventUuid: string) {
    return api.get(`/events/${eventUuid}/posts/attachments/${uuid}`);
  }

  /**
   * Deletes an attachment.
   * @param uuid the uuid of the attachment
   * @param eventUuid the uuid of the attachment's post's event
   */
  delete(uuid: string, eventUuid: string) {
    return api.delete(`/events/${eventUuid}/posts/attachments/${uuid}`);
  }
}

export default new AttachmentsApi();
