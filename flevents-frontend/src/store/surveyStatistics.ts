import { defineStore } from 'pinia'
import AccountApi from "@/api/accountsApi";
import {FleventsEvent} from "@/models/fleventsEvent";
import {useAccountStore} from "@/store/account";
import {Account} from "@/models/account";
import eventApi from "@/api/eventsApi";
import {computed} from "vue";
import {STORES} from "@/constants";
import questionnaireApi from "@/api/questionnaireApi";

export const useSurveyStatisticsStore = defineStore('surveyStatistics', {
  state: () => ({
    cachedStatistics: new Map<string, Object>,
    specificLoading: new Map<string, boolean>,
    specificError: new Map<string, boolean>,
    lastCaching: new Map<string, Date>,

    loading: false,
  }),
  actions: {
    async hydrateSpecific(questionnaireUuid: string) {
      this.specificLoading.set(questionnaireUuid, true);
      try {
        const response = await questionnaireApi.getStatistics(questionnaireUuid);
        this.cachedStatistics.set(questionnaireUuid, response.data as FleventsEvent);
        this.lastCaching.set(questionnaireUuid, new Date());
        this.specificError.set(questionnaireUuid, false);
      } catch (e) {
        console.warn(`Failed to fetch statistics of questionnaire with id ${questionnaireUuid}.`, e);
        this.specificError.set(questionnaireUuid, true);
      } finally {
        this.specificLoading.set(questionnaireUuid, false);
      }
    },

    /**
     * Retrieves statistics.
     * If the requested statistics are not yet loaded, it initializes their loading.
     * @param questionnaireUuid the uuid of the questionnaire
     * @returns The statistics if cached, `undefined` otherwise.
     */
    getStatisticsOf(questionnaireUuid: string) {
      const requestedStatistics = this.cachedStatistics.get(questionnaireUuid);
      const lastUpdate = this.lastCaching.get(questionnaireUuid);
      if (
        requestedStatistics === undefined
        || lastUpdate !== undefined && new Date().getTime() - lastUpdate.getTime() > STORES.CACHE_MAX_AGE
      ) {
        this.hydrateSpecific(questionnaireUuid);
      }
      return requestedStatistics || {} as Object;
    },

    /**
     * Creates a computed ref that returns the most recent statistics of the questionnaire.
     * @param questionnaireUuid the uuid of the questionnaire
     */
    getStatisticsGetterOf(questionnaireUuid: string) {
      return computed(() => this.getStatisticsOf(questionnaireUuid),);
    },

    async dehydrate() {
      this.loading = false;
      this.cachedStatistics = new Map();
      this.lastCaching = new Map();
      this.specificLoading = new Map();
      this.specificError = new Map();
    }
  },
});
