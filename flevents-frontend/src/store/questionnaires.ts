import {defineStore, storeToRefs} from 'pinia'
import {Questionnaire} from "@/models/questionnaire";
import {STORES} from "@/constants";
import questionnaireApi from "@/api/questionnaireApi";
import {computed} from "vue";
import {useEventStore} from "@/store/events";

/**
 * Store for state management of Questionnaires.
 * @author David Maier
 * @since Weekly Build 3
 */
export const useQuestionnaireStore = defineStore('questionnaires', {
  state: () => ({
    cachedQuestionnairesOfEvents: new Map<string, string[]>(),

    cachedQuestionnaires: new Map<string, Questionnaire>(),
    lastCaching: new Map<string, Date>,
    specificLoading: new Map<string, boolean>,
    specificError: new Map<string, boolean>,

    lastSuccessfulHydration: undefined as Date|undefined,
    loading: false,
    error: false,
  }),
  actions: {

    /**
     * Hydrates the store by requesting data from the event store.
     * This way, advantage is taken of already cached data.
     */
    async hydrate() {
      if (this.loading === true) {
        // Do not hydrate if already hydrating
        return;
      }
      this.error = false;
      this.loading = true;
      const eventStore = useEventStore();
      const { cachedEvents } = storeToRefs(eventStore);
      cachedEvents.value.forEach(e => {
        this.cachedQuestionnairesOfEvents.set(e.uuid!, e.questionnaires.map(q => q.uuid));
        e.questionnaires.forEach(q =>{
          this.cachedQuestionnaires.set(q.uuid, q);
        });
      });
      this.lastSuccessfulHydration = new Date();
      this.error = false;
      this.loading = true;
    },

    /**
     * Signals the store that recent contents of it's state are being used.
     * The store then decides whether the last successful hydration is long enough ago to update the state.
     */
    async requestHydration() {
      if (
        this.lastSuccessfulHydration === undefined
        || new Date().getTime() - this.lastSuccessfulHydration.getTime() > STORES.CACHE_MAX_AGE
      ) {
        await this.hydrate();
      }
    },

    /**
     * Hydrates the store by requesting a specific questionnaire from the api.
     * @param uuid the uuid of the questionnaire
     */
    async hydrateSpecific(uuid: string) {
      if (this.specificLoading.get(uuid) === true) {
        // Do not hydrate if already hydrating
        return;
      }
      this.specificLoading.set(uuid, true);
      try {
        const { data } = await questionnaireApi.get(uuid);
        this.cachedQuestionnaires.set(uuid, data as Questionnaire);
        this.lastCaching.set(uuid, new Date());
        this.specificError.set(uuid, false);
      } catch (e) {
        console.warn(`Failed to fetch questionnaires for event with id ${uuid}.`, e);
        this.specificError.set(uuid, true);
      }
      this.specificLoading.set(uuid, false);
    },

    /**
     * Hydrates the store by requesting the posts of the given event uuid from the api.
     * @param eventUuid the uuid of the event associated with requested posts
     */
    async hydrateSpecificOf(eventUuid: string) {
      if (this.specificLoading.get(eventUuid) === true) {
        // Do not hydrate if already hydrating
        return;
      }
      this.specificLoading.set(eventUuid, true);
      try {
        const { data } = await questionnaireApi.getOf(eventUuid);
        const questionnairesOfEvent = data as Questionnaire[];
        questionnairesOfEvent.forEach(q => {
          if (q.uuid) {
            this.cachedQuestionnaires.set(q.uuid, q);
            this.lastCaching.set(q.uuid, new Date());
            this.specificError.set(q.uuid, false);
          }
        });
        this.cachedQuestionnairesOfEvents.set(
          eventUuid,
          questionnairesOfEvent.map(q => q.uuid)
        );
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
      const requestedQuestionnaires = this.cachedQuestionnairesOfEvents.get(eventUuid);
      const lastUpdate = this.lastCaching.get(eventUuid);
      if (
        requestedQuestionnaires === undefined
        || lastUpdate !== undefined && new Date().getTime() - lastUpdate.getTime() > STORES.CACHE_MAX_AGE
      ) {
        this.hydrateSpecificOf(eventUuid);
      }
      return requestedQuestionnaires?.map(pUuid => this.cachedQuestionnaires.get(pUuid) as Questionnaire) || [] as Questionnaire[];
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
      const requestedQuestionnaire = this.cachedQuestionnaires.get(uuid);
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
      this.cachedQuestionnaires.set(questionnaire.uuid, questionnaire);
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
      this.cachedQuestionnairesOfEvents = new Map();
      this.cachedQuestionnaires = new Map();
      this.lastCaching = new Map();
      this.specificLoading = new Map();
      this.specificError = new Map();
      this.lastSuccessfulHydration = undefined;
      this.loading = false;
      this.error = false;
    }
  },
})
