import { defineStore } from 'pinia'
import api from "@/api/api";
import {Account} from "@/models/account";
import {useAppStore} from "@/store/app";
import {STORES} from "@/constants";
import AccountApi from "@/api/accountsApi";

/**
 * Stores the events received from the API.
 *
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
    async hydrate() {
      this.loading = true;
      this.error = false;
      const appStore = useAppStore();
      if (!appStore.currentAccountId) {
        throw Error('There is no logged in account present.');
      }
      try {
        const { data } = await AccountApi.getMe();
        console.log(data);
        this.currentAccount = data as Account;
        this.lastSuccessfulHydration = new Date();
      } catch (e) {
        console.log(e);
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
