import {defineStore, storeToRefs} from 'pinia'
import {Post} from "@/models/post";
import {STORES} from "@/constants";
import postApi from "@/api/postsApi";
import {computed} from "vue";
import {useEventStore} from "@/store/events";

/**
 * Store for state management of Posts.
 * @author David Maier
 * @since Weekly Build 3
 */
export const usePostStore = defineStore('posts', {
  state: () => ({
    cachedPostsOfEvents: new Map<string, string[]>(),

    cachedPosts: new Map<string, Post>(),
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
        this.cachedPostsOfEvents.set(e.uuid!, e.posts.map(p => p.uuid));
        e.posts.forEach(p =>{
          this.cachedPosts.set(p.uuid, p);
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
     * Hydrates the store by requesting the posts of the given event uuid from the API.
     * @param eventUuid the uuid of the event associated with requested posts
     */
    async hydrateSpecificOf(eventUuid: string) {
      if (this.specificLoading.get(eventUuid) === true) {
        // Do not hydrate if already hydrating
        return;
      }
      this.specificLoading.set(eventUuid, true);
      try {
        const { data } = await postApi.getOf(eventUuid);
        let postsOfEvent = data as Post[];
        postsOfEvent.forEach(p => {
          this.cachedPosts.set(p.uuid, p);
          this.lastCaching.set(p.uuid, new Date());
          this.specificError.set(p.uuid, false);
        });
        this.cachedPostsOfEvents.set(
          eventUuid,
          postsOfEvent.map(p => p.uuid)
        );
        this.lastCaching.set(eventUuid, new Date());
        this.specificError.set(eventUuid, false);
      } catch (e) {
        console.warn(`Failed to fetch posts for event with id ${eventUuid}.`, e);
        this.specificError.set(eventUuid, true);
        this.error = true;
      }
      this.specificLoading.set(eventUuid, false);
    },

    /**
     * Retrieves all posts associated with the event with given uuid.
     * Initializes the retrieval of the posts if they have not yet been loaded.
     * @param eventUuid the uuid of the event associated with requested posts
     * @returns an array of posts
     */
    getPostsOf(eventUuid: string) {
      const requestedPosts = this.cachedPostsOfEvents.get(eventUuid);
      const lastUpdate = this.lastCaching.get(eventUuid);
      if (
        requestedPosts === undefined
        || lastUpdate !== undefined && new Date().getTime() - lastUpdate.getTime() > STORES.CACHE_MAX_AGE
      ) {
        this.hydrateSpecificOf(eventUuid);
      }
      return requestedPosts?.map(pUuid => this.cachedPosts.get(pUuid) as Post) || [] as Post[];
    },

    /**
     * Creates a computed ref that returns the posts of the event.
     * @param eventUuid the uuid of the event
     */
    getPostsGetterOf(eventUuid: string) {
      return computed(() => this.getPostsOf(eventUuid),);
    },

    /**
     * Retrieves a post.
     * If the requested post is not yet loaded, it initialized its loading.
     * @param uuid the uuid of the post
     * @param eventUuid the uuid of the event associated with requested post
     * @returns The post if cached, `undefined` otherwise.
     */
    getPost(uuid: string, eventUuid: string) {
      const requestedPost = this.cachedPosts.get(uuid);
      const lastUpdate = this.lastCaching.get(uuid);
      if (
        requestedPost === undefined
        || lastUpdate !== undefined && new Date().getTime() - lastUpdate.getTime() > STORES.CACHE_MAX_AGE
      ) {
        this.hydrateSpecificOf(eventUuid);
      }
      return requestedPost || {} as Post;
    },

    /**
     * Changes the cached post to the given one.
     * If one of `post` or `post.uuid` is `undefined` no action is taken.
     * @param post the event
     */
    setPost(post: Post|undefined) {
      if (post === undefined || post.uuid === undefined) return;
      this.cachedPosts.set(post.uuid, post);
    },

    /**
     * Creates a writable computed ref that enables read/write access to a cached post.
     * @param uuid the uuid of the questionnaire
     * @param eventUuid the uuid of the post's event
     */
    getPostGetter(uuid: string, eventUuid: string) {
      return computed({
        get: () => this.getPost(uuid, eventUuid),
        set: (e) => this.setPost(e),
      });
    },

    async dehydrate() {
      this.cachedPostsOfEvents = new Map();
      this.cachedPosts = new Map();
      this.lastCaching = new Map();
      this.specificLoading = new Map();
      this.specificError = new Map();
      this.lastSuccessfulHydration = undefined;
      this.loading = false;
      this.error = false;
    }
  },
})
