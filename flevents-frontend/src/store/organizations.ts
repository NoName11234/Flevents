import { defineStore } from 'pinia'
import AccountApi from "@/api/accountApi";
import Security from "@/service/security";
import {Organization} from "@/models/organization";

export const useOrganizationStore = defineStore('organizations', {
  state: () => ({
    managedOrganizations: [] as Organization[],
    loading: false,
    error: false,
  }),
  actions: {
    async hydrate() {
      this.error = false;
      this.loading = true;
      const account = Security.getAccount();
      if (account === null) {
        console.error('Cannot get events without logged-in user.');
        this.error = true;
        this.loading = false;
        return;
      }
      try {
        const response = await AccountApi.getManagedOrganizations(account.uuid);
        console.log(response);
        this.managedOrganizations = response.data as Organization[];
      } catch (error) {
        console.warn('Failed to fetch events of logged-in user.');
        this.error = true;
      } finally {
        this.loading = false;
      }
    },
    getOrganization(uuid: string) {
      return this.managedOrganizations.find(e => e.uuid === uuid);
    },
  },
})
