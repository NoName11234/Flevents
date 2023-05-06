import api from "@/api/api";
import {Account} from "@/models/account";

const base = `/accounts`

/**
 * Api endpoints to access and modify accounts.
 * @author David Maier
 * @since Weekly Build 2
 */
class AccountsApi {

  // CRUD

  /**
   * Creates an account.
   * @param account the account to be created
   */
  create(account: Account) {
    if (account.email)
      account.email = account.email.toLowerCase();
    return api.post(`${base}`, account);
  }

  // /**
  //  * Retrieves an account.
  //  * @param uuid the uuid of the account
  //  * @deprecated
  //  */
  // get(uuid: string) {
  //   return api.get(`${base}/${uuid}`);
  // }
  //
  // /**
  //  * Modifies the account with the given uuid.
  //  * @param uuid the uuid of the account
  //  * @param account the account object containing only the attributes to be modified
  //  */
  // edit(uuid: string, account: Account) {
  //   return api.post(`${base}/${uuid}`, account);
  // }
  //
  // /**
  //  * Deletes an account.
  //  * @param uuid the uuid of the account
  //  */
  // delete(uuid: string) {
  //   return api.delete(`${base}/${uuid}`);
  // }

  /**
   * Returns the currently logged-in account.
   */
  getMe() {
    return api.get(`${base}`);
  }

  /**
   * Modified the currently logged-in account.
   * @param account the modified account
   */
  editMe(account: Account) {
    if (account.email)
      account.email = account.email.toLowerCase();
    return api.put(`${base}`, account);
  }

  /**
   * Deletes the currently logged-in account.
   */
  deleteMe() {
    return api.delete(`${base}`);
  }



  // Security

  /**
   * Tries to authenticate using given details.
   * @param email a valid email-address
   * @param secret a the corresponding encoded password
   */
  login(email: string, secret: string) {
    if (email)
      email = email.toLowerCase();
    return api.post(`${base}/login`, {
      username: email,
      password: secret
    });
  }

  /**
   * Renews the current authentication.
   */
  refresh() {
    return api.post(`${base}/refresh`);
  }

  /**
   * Logs the user out and ends the session.
   */
  logout(token: string) {
    return api.post(`${base}/logout`, {
      token: token,
    });
  }



  // Miscellaneous

  /**
   * Sends a password-reset mail for the given account.
   * @param mailAddress the mailAddress of the account
   */
  resetPassword(mailAddress: string) {
    return api.post(`${base}/reset-password`, {}, {params : {email: mailAddress}});
  }


  // Collections

  /**
   * Retrieves the booked events of the given account (i.e. all events where it is registered).
   * @param uuid the uuid of the account
   */
  getBookedEvents(uuid: string) {
    return api.get(`${base}/booked-events`);
  }

  /**
   * Retrieves the managed events of the given account (i.e. all events it is permitted to edit).
   * @param uuid the uuid of the account
   */
  getManagedEvents(uuid: string) {
    return api.get(`${base}/managed-events`);
  }

  /**
   * Retrieves the explore-events of the given account (i.e. all events of organizations it is associated with).
   * @param uuid the uuid of the account
   */
  getExploreEvents(uuid: string) {
    return api.get(`${base}/explore-events`);
  }

  /**
   * Retrieves the managed organizations of the given account (i.e. all events where it is administrator).
   * @param uuid the uuid of the account
   */
  getManagedOrganizations(uuid: string) {
    return api.get(`${base}/managed-organizations`);
  }


}

export default new AccountsApi();
