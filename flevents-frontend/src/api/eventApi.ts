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
   * @param organizationUuid the uuid of the organization the event belongs to
   */
  create(event: FleventsEvent, organizationUuid: string) {
    return api.post(`/organizations/${organizationUuid}/create-event`, event);
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
    return api.post(`${base}/${uuid}`, event);
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
   * @param email the e-mail of the person to be invited
   * @param role the role the account will have
   */
  inviteAttendee(uuid: string, email: string, role: EventRole.attendee|EventRole.tutor) {
    return api.post(`${base}/${uuid}/invite`, {}, {
      params: {
        email: email,
        role: role,
      }
    });
  }

  /**
   * Invites an account to be associated with the event as organizer.
   * @param uuid the uuid of the event
   * @param email the e-mail of the person to be invited
   */
  inviteOrganizer(uuid: string, email: string) {
    return api.post(`${base}/${uuid}/invite`, {}, {
      params: {
        email: email,
        role: EventRole.organizer,
      }
    });
  }

  /**
   * Accepts an invitation link as the current user.
   * @param uuid the uuid of the event
   * @param token the token the account has been invited with
   */
  acceptInvitation(uuid: string, token: string) {
    return api.post(`${base}/${uuid}/accept-invitation`, {}, {
      params: {
        token: token,
      }
    });
  }

  /**
   * Enrolls the current user to the event.
   * @param uuid the uuid of the event
   */
  addAccount(uuid: string) {
    return api.post(`${base}/${uuid}/add-account`);
  }

  /**
   * Dis-enrolls the current user from the event.
   * @param uuid the uuid of the event
   * @param accountUuid the uuid of the account
   * @param role the role the account was previously associated with the event
   */
  removeAccount(uuid: string, accountUuid: string, role: EventRole) {
    return api.post(`${base}/${uuid}/remove-account/${accountUuid}`, {}, {
      params: {
        role: role,
      }
    });
  }
}

export default new EventApi();
