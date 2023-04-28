import { defineStore } from 'pinia'
// import surveyApi from "@/api/surveyApi";
import {Questionnaire} from "@/models/questionnaire";
import {STORES} from "@/constants";

export const useSurveyStore = defineStore('surveys', {
  state: () => ({
    surveys: new Map<string, Questionnaire[]>(),
    lastSuccessfulHydration: new Map<string, Date>(),
    loading: false,
    error: false,
  }),
  actions: {

    /**
     * Hydrates the store by requesting the posts of the given event uuid from the api.
     * @param eventUuid the uuid of the event associated with requested posts
     */
    async hydrateSpecific(eventUuid: string) {
      this.loading = true;
      this.error = false;
      // TODO: create and use posts store
      try {
        // const { data } = await surveyApi.get(eventUuid);
        // this.surveys.set(eventUuid, data as Questionnaire[]);
        // this.lastSuccessfulHydration.set(eventUuid, new Date());
      } catch (e) {
        console.warn(`Failed to fetch surveys for event with id ${eventUuid}.`, e);
        this.error = true;
      }
      this.loading = false;
    },

    /**
     * Retrieves all questionnaires associated with the event with given uuid.
     * Initializes the retrieval of the questionnaires if they have not yet been loaded.
     * @param eventUuid the uuid of the event associated with requested questionnaires
     * @returns an array of questionnaires
     */
    getSurveys(eventUuid: string) {
      const requestedSurveys = this.surveys.get(eventUuid);
      const lastUpdate = this.lastSuccessfulHydration.get(eventUuid);
      if (
        requestedSurveys === undefined
        || lastUpdate !== undefined && new Date().getTime() - lastUpdate.getTime() > STORES.CACHE_MAX_AGE
      ) {
        this.hydrateSpecific(eventUuid);
      }
      return requestedSurveys || [] as Questionnaire[];
    },
  },
})
