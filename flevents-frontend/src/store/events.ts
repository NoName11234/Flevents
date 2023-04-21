import { defineStore } from 'pinia'
import AccountApi from "@/api/accountApi";
import Security from "@/service/security";
import {FleventsEvent} from "@/models/fleventsEvent";

export const useEventStore = defineStore('events', {
  state: () => ({
    bookedEvents: [] as FleventsEvent[],
    managedEvents: [] as FleventsEvent[],
    exploreEvents: [] as FleventsEvent[],
    cachedEvents: [] as FleventsEvent[],
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
      const account = Security.getAccount();
      if (account === null) {
        console.error('Cannot get events without logged-in user.');
        this.error = true;
        this.loading = false;
        return;
      }
      try {
        const responses = await Promise.all([
          AccountApi.getBookedEvents(account.uuid),
          AccountApi.getManagedEvents(account.uuid),
          AccountApi.getExploreEvents(account.uuid)
        ]);
        this.bookedEvents = responses[0].data as FleventsEvent[];
        this.managedEvents = responses[1].data as FleventsEvent[];
        this.exploreEvents = responses[2].data as FleventsEvent[];
        this.lastSuccessfulHydration = new Date();
      } catch (e) {
        console.warn('Failed to fetch events of logged-in user.', e);
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
     * Retrieves an event.
     * @param uuid the uuid of the event
     */
    getEvent(uuid: string) {
      return this.managedEvents
        .concat(this.bookedEvents, this.bookedEvents)
        .find(e => e.uuid === uuid);
    },
  },
})
