// Utilities
import { defineStore } from 'pinia'
import api from "@/api";
import {useAccountStore} from "@/store/account";

/**
 * Stores the booked events of the currently logged-in user received from the API.
 *
 */
export const useEventStore = defineStore({
  id: 'eventStore',
  state: () => ({
    bookedEvents: [],
    loading: false,
  }),
  actions: {
    async hydrate() {
      this.loading = true;
      const account = useAccountStore().currentAccount;
      if (account === null) {
        throw Error('Cannot request booked events without logged-in account.');
      }
      try {
        const { data } = await api.get(`/accounts/${account.uuid}/booked-events`);
        this.bookedEvents = data;
      } catch (e) {
        console.log(e)
      } finally {
        this.loading = false;
      }
    },
  },
})
