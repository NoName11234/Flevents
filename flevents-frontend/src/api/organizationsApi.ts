import api from "@/api/api";
import {Organization} from "@/models/organization";
import {OrganizationRole} from "@/models/organizationRole";
import {ro} from "vuetify/locale";

const base = `/organizations`

/**
 * Api endpoints to access and modify accounts.
 * @author David Maier
 * @since Weekly Build 2
 */
class OrganizationApi {

  // CRUD

  /**
   * Creates an organization.
   * @param organization the organization to be created
   */
  create(organization: Organization) {
    return api.post(`${base}`, organization);
  }

  /**
   * Retrieves an organizations.
   * @param uuid the uuid of the account
   */
  get(uuid: string) {
    return api.get(`${base}/${uuid}`);
  }

  /**
   * Modifies an organization.
   * @param uuid the uuid of the organization
   * @param organization the organization object containing only the attributes to be modified
   */
  edit(uuid: string, organization: Organization) {
    return api.post(`${base}/${uuid}`, organization);
  }

  /**
   * Deletes an organization.
   * @param uuid the uuid of the organization
   */
  delete(uuid: string) {
    return api.delete(`${base}/${uuid}`);
  }



  // Member-Management

  /**
   * Retrieves all members of an organization.
   * @param uuid the uuid of the organization
   */
  getMembers(uuid: string) {
    return api.post(`${base}/${uuid}/accounts`);
  }

  /**
   * Removes a member from an organization.
   * @param uuid the uuid of the organization
   * @param accountUuid the uuid of the account
   */
  removeMember(uuid: string, accountUuid: string) {
    return api.post(`${base}/${uuid}/remove-account/${accountUuid}`);
  }

  /**
   * Sends an invitation to the given email-address.
   * @param uuid the uuid of the organization
   * @param email an email-address of the person to be invited
   * @param role the role the account should be associated with the organization
   */
  invite(uuid: string, email: string, role: OrganizationRole = OrganizationRole.member) {
    return api.post(`${base}/${uuid}/invite`, {}, {
      params: {
        email,
        role,
      }
    });
  }

  /**
   * Adds the account with given accountUuid to the event with given uuid if the token is valid.
   * @param uuid the uuid of the organization
   * @param token the token the account has been invited with
   */
  acceptInvitation(uuid: string, token: string) {
    return api.post(`${base}/${uuid}/add-account`, {}, {
      params: {
        token,
      }
    });
  }

  /**
   * Leaves the organization as the current user.
   */
  leave(uuid: string, accountUuid: string) {
    return api.post(`${base}/${uuid}/leave-organization/${accountUuid}`);
  }

  /**
   * Changes the role of an organization member.
   * @param uuid the uuid of the organization
   * @param accountUuid the uuid of the account
   * @param fromRole the previous role
   * @param toRole the future role
   */
  changeRole(uuid: string, accountUuid: string, fromRole: OrganizationRole, toRole: OrganizationRole) {
    return api.post(`${base}/${uuid}/change-role/${accountUuid}`, {}, {
      params: {
        fromRole: fromRole,
        toRole: toRole,
      }
    });
  }



  // Collections

  getEvents(uuid: string) {
    return api.get(`${base}/${uuid}/events`);
  }
}

export default new OrganizationApi();
