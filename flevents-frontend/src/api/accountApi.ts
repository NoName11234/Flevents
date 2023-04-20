import api from "@/api/api";
import {Account} from "@/models/account";

const base = `/accounts`

/**
 * Api endpoints to access and modify accounts.
 * @author David Maier
 */
class AccountApi {
  /**
   * Creates an account.
   * @param account the account to be created
   */
  create(account: Account) {
    return api.post(`${base}`, account);
  }

  /**
   * Retrieves an account.
   * @param uuid the uuid of the account
   */
  get(uuid: string) {
    return api.get(`${base}/${uuid}`);
  }

  /**
   * Modifies the account with the given uuid.
   * @param uuid the uuid of the account
   * @param account the account object containing the information to be modified
   */
  edit(uuid: string, account: Account) {
    return api.put(`${base}/${uuid}`, account);
  }

  /**
   * Deletes an account.
   * @param uuid the uuid of the account
   */
  delete(uuid: string) {
    return api.delete(`${base}/${uuid}`);
  }

  /**
   * Sends a password-reset mail for the given account.
   * @param uuid the uuid of the account
   */
  resetPassword(uuid: string) {
    return api.post(`${base}/${uuid}`);
  }

  /**
   * Retrieves the booked events of the given account (i.e. all events where it is registered).
   * @param uuid the uuid of the account
   */
  getBookedEvents(uuid: string) {
    return api.get(`${base}/${uuid}/booked-events`);
  }

  /**
   * Retrieves the managed events of the given account (i.e. all events it is permitted to edit).
   * @param uuid the uuid of the account
   */
  getManagedEvents(uuid: string) {
    return api.get(`${base}/${uuid}/managed-events`);
  }

  /**
   * Retrieves the explore-events of the given account (i.e. all events of organizations it is associated with).
   * @param uuid the uuid of the account
   */
  getExploreEvents(uuid: string) {
    return api.get(`${base}/${uuid}/explore-events`);
  }

  /**
   * Retrieves the managed organizations of the given account (i.e. all events where it is administrator).
   * @param uuid the uuid of the account
   */
  getManagedOrganizations(uuid: string) {
    return api.get(`${base}/${uuid}/managed-organizations`);
  }
}

export default new AccountApi();
