// Utilities
import { defineStore } from 'pinia'
import api from "@/api";
import {Account} from "@/models/account";

/**
 * Stores the events received from the API.
 *
 */
export const useAccountStore = defineStore({
  id: 'accountStore',
  state: () => ({
    currentAccount: null as Account|null,
    loading: false,
  }),
  actions: {
    async hydrate() {
      this.loading = true;
      if (this.currentAccount === null) {
        throw Error('There is no logged in account present.');
      }
      try {
        const { data } = await api.get(`/accounts/${this.currentAccount.uuid}`);
        this.currentAccount = data;
      } catch (e) {
        console.log(e)
      } finally {
        this.loading = false;
      }
    },
  },
})
