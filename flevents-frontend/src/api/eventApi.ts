import api from "@/api/api";
import {FleventsEvent} from "@/models/fleventsEvent";
import {EventRole} from "@/models/eventRole";

const base = `/events`

/**
 * Api endpoints to access and modify events.
 * @author David Maier
 * @since Weekly Build 2
 */
class EventApi {

  // CRUD

  /**
   * Creates an event.
   * @param event the account to be created.
   */
  create(event: FleventsEvent) {
    return api.post(`${base}`, event);
  }

  /**
   * Retrieves an event.
   * @param uuid the uuid of the event.
   */
  get(uuid: string) {
    return api.get(`${base}/${uuid}`);
  }

  /**
   * Modifies the event with the given uuid.
   * @param uuid the uuid of the account
   * @param event the event object containing only the attributed to be modified
   */
  edit(uuid: string, event: FleventsEvent) {
    return api.put(`${base}/${uuid}`, event);
  }

  /**
   * Deletes an event.
   * @param uuid the uuid of the event
   */
  delete(uuid: string) {
    return api.delete(`${base}/${uuid}`);
  }



  // Organizer-Management

  /**
   * Retrieves all organizers of an event.
   * @param uuid the uuid of the event.
   */
  getOrganizers(uuid: string) {
    return api.get(`${base}/${uuid}/organizers`);
  }

  /**
   * Adds the account with given accountUuid to the event with given uuid if the token is valid.
   * @param uuid the uuid of the event
   * @param accountUuid the uuid of the account to be added
   * @param token the token the account has been invited with
   */
  addOrganizer(uuid: string, accountUuid: string, token: string) {
    return api.post(`${base}/${uuid}/organizers`, {}, {
      params: {
        accountId: accountUuid,
        token,
      }
    });
  }

  /**
   * Removes an organizer from an event.
   * @param uuid the uuid of the event
   * @param accountUuid the uuid of the account to be removed
   */
  removeOrganizer(uuid: string, accountUuid: string) {
    return api.delete(`${base}/organizers/${accountUuid}`);
  }



  // Attendee-Management

  /**
   * Retrieves all attendees that take part in an event (including tutors).
   * @param uuid the uuid of the event
   */
  getAttendees(uuid: string) {
    return api.get(`${base}/${uuid}/attendees`);
  }

  /**
   * Invites an account to be associated with the event under the given role.
   * @param uuid the uuid of the event
   * @param accountUuid the uuid of the account to be invited
   * @param role the role the account will have
   */
  inviteAttendee(uuid: string, accountUuid: string, role: EventRole.attendee|EventRole.tutor) {
    return api.post(`${base}/${uuid}/invite`, {}, {
      params: {
        accountId: accountUuid,
        role: role,
      }
    });
  }
}

export default new EventApi();
