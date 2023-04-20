import { defineStore } from 'pinia'
import AccountService from "@/service/accountService";
import Security from "@/service/security";
import {FleventsEvent} from "@/models/fleventsEvent";

export const useEventStore = defineStore('events', {
  state: () => ({
    bookedEvents: [] as FleventsEvent[],
    managedEvents: [] as FleventsEvent[],
    exploreEvents: [] as FleventsEvent[],
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
        const responses = await Promise.all([
          AccountService.getBookedEvents(account.uuid),
          AccountService.getManagedEvents(account.uuid),
          AccountService.getExploreEvents(account.uuid)
        ]);
        console.log(responses);
        this.bookedEvents = responses[0].data as FleventsEvent[];
        this.managedEvents = responses[1].data as FleventsEvent[];
        this.exploreEvents = responses[2].data as FleventsEvent[];
      } catch (error) {
        console.warn('Failed to fetch events of logged-in user.');
        this.error = true;
      } finally {
        this.loading = false;
      }
    },
    getEvent(uuid: string) {
      return this.bookedEvents
        .concat(this.managedEvents, this.exploreEvents)
        .find(e => e.uuid === uuid);
    },
  },
})
