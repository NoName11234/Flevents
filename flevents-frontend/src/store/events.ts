import { defineStore } from 'pinia'
import AccountApi from "@/api/accountApi";
import {FleventsEvent} from "@/models/fleventsEvent";
import {useAccountStore} from "@/store/account";
import {Account} from "@/models/account";
import eventApi from "@/api/eventApi";
import {computed} from "vue";

export const useEventStore = defineStore('events', {
  state: () => ({
    bookedEvents: [] as FleventsEvent[],
    managedEvents: [] as FleventsEvent[],
    exploreEvents: [] as FleventsEvent[],
    lastSuccessfulHydration: undefined as Date|undefined,

    cachedEvents: new Map<string, FleventsEvent>,
    specificLoading: new Map<string, boolean>,
    lastCaching: new Map<string, Date>,

    loading: false,
    error: false,
  }),
  actions: {

    /**
     * Hydrates the store by requesting the data from the api.
     */
    async hydrate() {
      this.error = false;
      this.loading = true;
      const accountStore = useAccountStore();
      const account = accountStore.currentAccount as Account;
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
        this.bookedEvents.concat(this.managedEvents, this.exploreEvents).forEach(e =>
          e.uuid ? this.cachedEvents.set(e.uuid, e) : undefined
        );
        this.lastSuccessfulHydration = new Date();
      } catch (e) {
        console.warn('Failed to fetch events of logged-in user.', e);
        this.error = true;
      } finally {
        this.loading = false;
      }
    },

    async hydrateSpecific(uuid: string) {
      this.error = false;
      this.specificLoading.set(uuid, true);
      try {
        const response = await eventApi.get(uuid);
        this.cachedEvents.set(uuid, response.data as FleventsEvent);
        this.lastCaching.set(uuid, new Date());
      } catch (e) {
        console.warn(`Failed to fetch event with id ${uuid}.`, e);
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
        || new Date().getTime() - this.lastSuccessfulHydration.getTime() > 60000
      ) {
        await this.hydrate();
      }
    },

    /**
     * Retrieves an event.
     * If the requested event is not yet loaded, it initialized its loading.
     * @param uuid the uuid of the event
     * @returns The event if cached, `undefined` otherwise.
     */
    getEvent(uuid: string) {
      const requestedEvent = this.cachedEvents.get(uuid);
      const lastUpdate = this.lastCaching.get(uuid);
      if (
        requestedEvent === undefined
        || lastUpdate !== undefined && new Date().getTime() - lastUpdate.getTime() > 60000
      ) {
        this.hydrateSpecific(uuid);
      }
      return requestedEvent || {} as FleventsEvent;
    },

    /**
     * Changes the cached event to the given one.
     * If one of `event` or `event.uuid` is `undefined` no action is taken.
     * @param event the event
     */
    setEvent(event: FleventsEvent|undefined) {
      if (event === undefined || event.uuid === undefined) return;
      this.cachedEvents.set(event.uuid, event);
      this.bookedEvents[
        this.bookedEvents.findIndex(e => e.uuid === event.uuid)
      ] = event;
      this.managedEvents[
        this.managedEvents.findIndex(e => e.uuid === event.uuid)
        ] = event;
      this.exploreEvents[
        this.exploreEvents.findIndex(e => e.uuid === event.uuid)
        ] = event;
    },

    getEventGetter(uuid: string) {
      return computed({
        get: () => this.getEvent(uuid),
        set: (e) => this.setEvent(e),
      });
    },

    async dehydrate() {
      this.loading = false;
      this.error = false;
      this.bookedEvents = [];
      this.managedEvents = [];
      this.exploreEvents = [];
      this.cachedEvents = new Map();
      this.lastSuccessfulHydration = undefined;
    }
  },
})
