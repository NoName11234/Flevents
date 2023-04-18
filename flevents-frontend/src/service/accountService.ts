import api from "@/api";
import {Account} from "@/models/account";

const base = `/accounts`

class AccountService {
  create(account: Account) {
    return api.post(`${base}`, account);
  }
  get(uuid: string) {
    return api.get(`${base}/${uuid}`);
  }
  edit(uuid: string, account: Account) {
    return api.put(`${base}/${uuid}`, account);
  }
  delete(uuid: string) {
    return api.delete(`${base}/${uuid}`);
  }
  resetPassword(uuid: string) {
    return api.post(`${base}/${uuid}`);
  }
  getBookedEvents(uuid: string) {
    return api.get(`${base}/${uuid}/booked-events`);
  }
  getManagedEvents(uuid: string) {
    return api.get(`${base}/${uuid}/managed-events`);
  }
  getExploreEvents(uuid: string) {
    return api.get(`${base}/${uuid}/explore-events`);
  }
  getManagedOrganizations(uuid: string) {
    return api.get(`${base}/${uuid}/managed-organizations`);
  }
}

export default new AccountService();
