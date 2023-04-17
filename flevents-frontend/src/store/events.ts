// Utilities
import { defineStore } from 'pinia'
import axios from "axios";

export const useEventStore = defineStore('events', {
  state: () => ({
    events: [],
  }),
  actions: {
    async fetchEvents() {
      const api = 'http://localhost:8082/api/events';
      try {
        const response = await axios.get(api);
        this.events = response.data;
        console.log(`Fetched events: ${JSON.stringify(this.events)}`);
      } catch (e) {
        console.log(e)
      }
    },
  },
  getters: {
    getEvents(state) {
      return state.events;
    }
  }
})
