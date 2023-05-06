import { defineStore } from 'pinia'
import {Questionnaire} from "@/models/questionnaire";
import {STORES} from "@/constants";
import questionnaireApi from "@/api/questionnaireApi";
import {computed} from "vue";

export const useSurveyStore = defineStore('surveys', {
  state: () => ({
    cachedSurveys: new Map<string, Questionnaire>(),
    cachedSurveysOfEvents: new Map<string, string[]>(),
    specificLoading: new Map<string, boolean>,
    specificError: new Map<string, boolean>,
    lastCaching: new Map<string, Date>,
  }),
  actions: {

    /**
     * Hydrates the store by requesting a specific questionnaire from the api.
     * @param uuid the uuid of the questionnaire
     */
    async hydrateSpecific(uuid: string) {
      this.specificLoading.set(uuid, true);
      try {
        const { data } = await questionnaireApi.get(uuid);
        this.cachedSurveys.set(uuid, data as Questionnaire);
        this.lastCaching.set(uuid, new Date());
        this.specificError.set(uuid, false);
      } catch (e) {
        console.warn(`Failed to fetch questionnaires for event with id ${uuid}.`, e);
        this.specificError.set(uuid, false);
      }
      this.specificLoading.set(uuid, false);
    },

    /**
     * Hydrates the store by requesting the posts of the given event uuid from the api.
     * @param eventUuid the uuid of the event associated with requested posts
     */
    async hydrateSpecificOf(eventUuid: string) {
      this.specificLoading.set(eventUuid, true);
      try {
        const { data } = await questionnaireApi.getOf(eventUuid);
        const questionnaires = data as Questionnaire[];
        questionnaires.forEach(q => {
          if (q.uuid) {
            this.cachedSurveys.set(q.uuid, q);
            this.lastCaching.set(q.uuid, new Date());
            this.specificError.set(q.uuid, false);
          }
        });
        this.cachedSurveysOfEvents.set(eventUuid, questionnaires.map(q => q.uuid));
        this.lastCaching.set(eventUuid, new Date());
        this.specificError.set(eventUuid, false);
      } catch (e) {
        console.warn(`Failed to fetch questionnaires of event with id ${eventUuid}.`, e);
        this.specificError.set(eventUuid, true);
      }
      this.specificLoading.set(eventUuid, false);
    },

    /**
     * Retrieves all questionnaires associated with the event with given uuid.
     * Initializes the retrieval of the questionnaires if they have not yet been loaded.
     * @param eventUuid the uuid of the event associated with requested questionnaires
     * @returns an array of questionnaires
     */
    getQuestionnairesOf(eventUuid: string) {
      const requestedQuestionnaires = this.cachedSurveys.get(eventUuid);
      const lastUpdate = this.lastCaching.get(eventUuid);
      if (
        requestedQuestionnaires === undefined
        || lastUpdate !== undefined && new Date().getTime() - lastUpdate.getTime() > STORES.CACHE_MAX_AGE
      ) {
        this.hydrateSpecificOf(eventUuid);
      }
      return requestedQuestionnaires || [] as Questionnaire[];
    },

    /**
     * Creates a computed ref that returns the questionnaires of the event.
     * @param eventUuid the uuid of the event
     */
    getQuestionnairesGetterOf(eventUuid: string) {
      return computed(() => this.getQuestionnairesOf(eventUuid),);
    },

    /**
     * Retrieves a questionnaire.
     * If the requested questionnaire is not yet loaded, it initializes its loading.
     * @param uuid the uuid of the questionnaire
     * @returns The event if cached, `undefined` otherwise.
     */
    getQuestionnaire(uuid: string) {
      const requestedQuestionnaire = this.cachedSurveys.get(uuid);
      const lastUpdate = this.lastCaching.get(uuid);
      if (
        requestedQuestionnaire === undefined
        || lastUpdate !== undefined && new Date().getTime() - lastUpdate.getTime() > STORES.CACHE_MAX_AGE
      ) {
        this.hydrateSpecific(uuid);
      }
      return requestedQuestionnaire || {} as Questionnaire;
    },

    /**
     * Changes the cached questionnaire to the given one.
     * If one of `questionnaire` or `questionnaire.uuid` is `undefined` no action is taken.
     * @param questionnaire the questionnaire
     */
    setQuestionnaire(questionnaire: Questionnaire) {
      if (questionnaire === undefined || questionnaire.uuid === undefined) return;
      this.cachedSurveys.set(questionnaire.uuid, questionnaire);
    },

    /**
     * Creates a writable computed ref that enables read/write access to a cached questionnaire.
     * @param uuid the uuid of the questionnaire
     */
    getQuestionnaireGetter(uuid: string) {
      return computed({
        get: () => this.getQuestionnaire(uuid),
        set: (q) => this.setQuestionnaire(q),
      });
    },

    async dehydrate() {
      this.cachedSurveys = new Map();
      this.cachedSurveysOfEvents = new Map<string, string[]>();
      this.lastCaching = new Map();
      this.specificLoading = new Map();
      this.specificError = new Map();
    }
  },
})
