import api from "@/api/api";
import {Organization} from "@/models/organization";
import {OrganizationRole} from "@/models/organizationRole";

const base = `/organizations`

class OrganizationApi {
  create(organization: Organization) {
    return api.post(`${base}`, organization);
  }
  get(uuid: string) {
    return api.get(`${base}/${uuid}`);
  }
  edit(uuid: string, organization: Organization) {
    return api.put(`${base}/${uuid}`, organization);
  }
  delete(uuid: string) {
    return api.delete(`${base}/${uuid}`);
  }
  getMembers(uuid: string) {
    return api.post(`${base}/${uuid}/accounts`);
  }
  invite(uuid: string, email: string, role: OrganizationRole = OrganizationRole.member) {
    return api.post(`${base}/${uuid}/managed-events`, {}, {
      params: {
        email,
        role,
      }
    });
  }
  addMember(uuid: string, token: string, accountUuid: string) {
    return api.post(`${base}/${uuid}/add-account/${accountUuid}`, {}, {
      params: {
        token,
      }
    });
  }
  removeMember(uuid: string, accountUuid: string) {
    return api.post(`${base}/${uuid}/remove-account/${accountUuid}`);
  }
  changeRole(uuid: string, accountUuid: string, oldRole: OrganizationRole, newRole: OrganizationRole) {
    return api.post(`${base}/${uuid}/change-role/${accountUuid}`, {}, {
      params: {
        oldRole,
        newRole,
      }
    });
  }
  getEvents(uuid: string) {
    return api.get(`${base}/${uuid}/events`);
  }
}

export default new OrganizationApi();
