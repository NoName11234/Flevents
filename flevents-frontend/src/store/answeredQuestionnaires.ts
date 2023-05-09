import { defineStore } from 'pinia'
import {STORES} from "@/constants";
import questionnaireApi from "@/api/questionnaireApi";
import {computed} from "vue";
import {AnsweredQuestionnaire} from "@/models/answeredQuestionnaire";
import {AxiosError, HttpStatusCode} from "axios";

/**
 * Store for state management of answers to questionnaires.
 * @author David Maier
 * @since Weekly Build 4
 */
export const useAnsweredQuestionnaireStore = defineStore('answeredQuestionnaires', {
  state: () => ({
    cachedAnsweredQuestionnaires: new Map<string, AnsweredQuestionnaire>(),
    specificLoading: new Map<string, boolean>,
    specificError: new Map<string, boolean>,
    lastCaching: new Map<string, Date>,
  }),
  actions: {

    /**
     * Hydrates the store by requesting a specific answered questionnaire from the api.
     * @param uuid the uuid of the respective questionnaire
     * @param accountUuid the uuid of the current account
     */
    async hydrateSpecific(uuid: string, accountUuid: string) {
      if (this.specificLoading.get(uuid) === true) {
        // Do not hydrate if already hydrating
        return;
      }
      this.specificLoading.set(uuid, true);
      try {
        const { data } = await questionnaireApi.getAnswers(uuid, accountUuid);
        this.cachedAnsweredQuestionnaires.set(uuid, data as AnsweredQuestionnaire);
        this.lastCaching.set(uuid, new Date());
        this.specificError.set(uuid, false);
      } catch (e) {
        //console.trace(`Questionnaire with id ${uuid} is not yet answered by ${accountUuid}.`, e);
        console.warn(`Failed to fetch answers for questionnaire with id ${uuid} for account ${accountUuid}.`, e);
        this.cachedAnsweredQuestionnaires.set(uuid, {} as AnsweredQuestionnaire);
        this.specificError.set(uuid, true);
      }
      this.specificLoading.set(uuid, false);
    },

    /**
     * Retrieves an answered questionnaire.
     * If the requested answered questionnaire is not yet loaded, it initializes its loading.
     * @param uuid the uuid of the questionnaire
     * @param accountUuid the uuid of the current account
     * @returns The event if cached, `undefined` otherwise.
     */
    getQuestionnaire(uuid: string, accountUuid: string) {
      const requestedAnsweredQuestionnaire = this.cachedAnsweredQuestionnaires.get(uuid);
      const lastUpdate = this.lastCaching.get(uuid);
      if (
        requestedAnsweredQuestionnaire === undefined
        || lastUpdate !== undefined && new Date().getTime() - lastUpdate.getTime() > STORES.CACHE_MAX_AGE
      ) {
        this.hydrateSpecific(uuid, accountUuid);
      }
      return requestedAnsweredQuestionnaire;
    },

    /**
     * Changes the cached answered questionnaire to the given one.
     * If one of `answeredQuestionnaire` or `answeredQuestionnaire.uuid` is `undefined` no action is taken.
     * @param answeredQuestionnaire the questionnaire
     */
    setQuestionnaire(answeredQuestionnaire: AnsweredQuestionnaire) {
      if (answeredQuestionnaire === undefined || answeredQuestionnaire.uuid === undefined) return;
      this.cachedAnsweredQuestionnaires.set(answeredQuestionnaire.uuid, answeredQuestionnaire);
    },

    /**
     * Creates a writable computed ref that enables read/write access to a cached answered questionnaire.
     * @param uuid the uuid of the questionnaire
     * @param accountUuid the uuid of the current account
     */
    getQuestionnaireGetter(uuid: string, accountUuid: string) {
      return computed({
        get: () => this.getQuestionnaire(uuid, accountUuid),
        set: (q) => q !== undefined ? this.setQuestionnaire(q) : undefined,
      });
    },

    async dehydrate() {
      this.cachedAnsweredQuestionnaires = new Map();
      this.lastCaching = new Map();
      this.specificLoading = new Map();
      this.specificError = new Map();
    }
  },
})
