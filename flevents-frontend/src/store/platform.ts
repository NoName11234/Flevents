import { defineStore } from 'pinia'
import {Organization} from "@/models/organization";
import {computed} from "vue";
import {STORES} from "@/constants";
import ConsoleApi from "@/api/consoleApi";
import OrganizationsApi from "@/api/organizationsApi";

export const usePlatformStore = defineStore('platform', {
  state: () => ({
    organizationIds: [] as string[],
    lastSuccessfulHydration: undefined as Date|undefined,

    cachedOrganizations: new Map<string, Organization>,
    specificLoading: new Map<string, boolean>,
    lastCaching: new Map<string, Date>,

    loading: false,
    error: false,
  }),
  getters: {
    organizations(state) {
      return state.organizationIds
        .map(id => state.cachedOrganizations.get(id) || undefined)
        .filter(e => e !== undefined) as Organization[];
    },
  },
  actions: {

    /**
     * Hydrates the store by requesting the data from the api.
     */
    async hydrate() {
      if (this.loading === true) {
        // Do not hydrate if already hydrating
        return;
      }
      this.error = false;
      this.loading = true;
      try {
        const response = await ConsoleApi.getAllOrganizations();
        const organizations = response.data as Organization[];
        organizations.forEach(o => {
          if (o.uuid) {
            this.cachedOrganizations.set(o.uuid, o);
            this.lastCaching.set(o.uuid, new Date());
          }
        });
        this.organizationIds = organizations.map(o => o.uuid!);
        this.lastSuccessfulHydration = new Date();
      } catch (error) {
        console.warn('Failed to fetch all organizations.');
        this.error = true;
      } finally {
        this.loading = false;
      }
    },

    async hydrateSpecific(uuid: string) {
      if (this.specificLoading.get(uuid) === true) {
        // Do not hydrate if already hydrating
        return;
      }
      this.error = false;
      this.specificLoading.set(uuid, true);
      try {
        const response = await OrganizationsApi.get(uuid);
        this.cachedOrganizations.set(uuid, response.data as Organization);
        this.lastCaching.set(uuid, new Date());
      } catch (e) {
        console.warn(`Failed to fetch organization with id ${uuid}.`, e);
        this.error = true;
      } finally {
        this.specificLoading.set(uuid, false);
      }
    },

    /**
     * Signals the store that recent contents of it's state are being used.
     * The store then decides whether the last successful hydration is long enough ago to update the state.
     */
    async requestHydration() {
      if (
        this.lastSuccessfulHydration === undefined
        || new Date().getTime() - this.lastSuccessfulHydration.getTime() > STORES.CACHE_MAX_AGE
      ) {
        await this.hydrate();
      }
    },

    /**
     * Retrieves a single organization of all loaded ones.
     * @param uuid the uuid of the organization
     */
    getOrganization(uuid: string) {
      const requestedOrganization = this.cachedOrganizations.get(uuid);
      const lastUpdate = this.lastCaching.get(uuid);
      if (
        requestedOrganization === undefined
        || lastUpdate !== undefined && new Date().getTime() - lastUpdate.getTime() > STORES.CACHE_MAX_AGE
      ) {
        this.hydrateSpecific(uuid);
      }
      return requestedOrganization || {} as Organization;
    },

    /**
     * Changes the cached organization to the given one.
     * If one of `event` or `event.uuid` is `undefined` no action is taken.
     * @param organization the organization
     */
    setOrganization(organization: Organization) {
      if (organization === undefined || organization.uuid === undefined) return;
      this.cachedOrganizations.set(organization.uuid, organization);
    },

    getOrganizationGetter(uuid: string) {
      return computed({
        get: () => this.getOrganization(uuid),
        set: (e) => this.setOrganization(e),
      });
    },

    async dehydrate() {
      this.loading = false;
      this.error = false;
      this.organizationIds = [];
      this.cachedOrganizations = new Map();
      this.lastCaching = new Map();
      this.specificLoading = new Map();
      this.lastSuccessfulHydration = undefined;
    }
  },
})
