import { defineStore } from 'pinia'
import AccountApi from "@/api/accountApi";
import Security from "@/service/security";
import {Organization} from "@/models/organization";

export const useOrganizationStore = defineStore('organizations', {
  state: () => ({
    managedOrganizations: [] as Organization[],
    loading: false,
    error: false,
    lastSuccessfulHydration: undefined as Date|undefined,
  }),
  actions: {

    /**
     * Hydrates the store by requesting the data from the api.
     */
    async hydrate() {
      this.error = false;
      this.loading = true;
      await new Promise(resolve => setTimeout(resolve, 1000));
      const account = Security.getAccount();
      if (account === null) {
        console.error('Cannot get events without logged-in user.');
        this.error = true;
        this.loading = false;
        return;
      }
      try {
        const response = await AccountApi.getManagedOrganizations(account.uuid);
        this.managedOrganizations = response.data as Organization[];
        this.lastSuccessfulHydration = new Date();
      } catch (error) {
        console.warn('Failed to fetch events of logged-in user.');
        this.error = true;
      } finally {
        this.loading = false;
      }
    },

    /**
     * Signals the store that recent contents of it's state are being used.
     * The store then decides whether the last successful hydration is long enough ago to update the state.
     */
    async requestHydration() {
      if (
        this.lastSuccessfulHydration === undefined
        || new Date().getTime() - this.lastSuccessfulHydration.getTime() > 60000
      ) {
        await this.hydrate();
      }
    },

    /**
     * Retrieves a single organization of all loaded ones.
     * @param uuid the uuid of the organization
     */
    getOrganization(uuid: string) {
      return this.managedOrganizations.find(e => e.uuid === uuid);
    },
  },
})
