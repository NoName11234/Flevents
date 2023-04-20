import api from "@/api/api";
import {FleventsEvent} from "@/models/fleventsEvent";
import {EventRole} from "@/models/eventRole";

const base = `/events`

class EventApi {

  // CRUD
  create(event: FleventsEvent) {
    return api.post(`${base}`, event);
  }
  get(uuid: string) {
    return api.get(`${base}/${uuid}`);
  }
  edit(uuid: string, event: FleventsEvent) {
    return api.put(`${base}/${uuid}`, event);
  }
  delete(uuid: string) {
    return api.delete(`${base}/${uuid}`);
  }

  // Organizer-Management
  getOrganizers(uuid: string) {
    return api.get(`${base}/${uuid}/organizers`);
  }
  addOrganizer(uuid: string, accountUuid: string, token: string) {
    return api.post(`${base}/${uuid}/organizers`, {}, {
      params: {
        accountId: accountUuid,
        token,
      }
    });
  }
  removeOrganizer(uuid: string, accountUuid: string) {
    return api.delete(`${base}/organizers/${accountUuid}`);
  }

  // Attendee-Management
  getAttendees(uuid: string) {
    return api.get(`${base}/${uuid}/attendees`);
  }
  inviteAttendee(uuid: string, accountUuid: string, role: EventRole.attendee|EventRole.tutor) {
    return api.post(`${base}/${uuid}/invite`, {}, {
      params: {
        accountId: accountUuid,
        role: EventRole.organizer,
      }
    });
  }
}

export default new EventApi();
