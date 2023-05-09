import { defineStore } from 'pinia'
import {Account} from "@/models/account";
import {STORES} from "@/constants";
import AccountApi from "@/api/accountsApi";

/**
 * Store for state management of the current Account.
 * @author David Maier
 * @since Weekly Build 1
 */
export const useAccountStore = defineStore({
  id: 'accountStore',
  state: () => ({
    currentAccount: null as Account|null,
    loading: false,
    error: false,
    lastSuccessfulHydration: undefined as Date|undefined,
  }),
  actions: {
    /**
     * Hydrates the store by requesting the data from the API.
     */
    async hydrate() {
      if (this.loading === true) {
        // Do not hydrate if already hydrating
        return;
      }
      this.loading = true;
      this.error = false;
      try {
        const { data } = await AccountApi.getMe();
        this.currentAccount = data as Account;
        this.lastSuccessfulHydration = new Date();
      } catch (e) {
        console.error(`Failed to load currently logged-in account.`, e);
        this.error = true;
      } finally {
        this.loading = false;
      }
    },
    async dehydrate() {
      this.loading = false;
      this.currentAccount = null;
      this.loading = false;
      this.lastSuccessfulHydration = undefined;
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
  },
})
