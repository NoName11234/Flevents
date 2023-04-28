import { defineStore } from 'pinia'
import {Post} from "@/models/post";
import postApi from "@/api/postsApi";
import {STORES} from "@/constants";
import {computed} from "vue";
import {FleventsEvent} from "@/models/fleventsEvent";

export const usePostStore = defineStore('posts', {
  state: () => ({
    postsOfEvents: new Map<string, string[]>(),
    lastSuccessfulHydration: new Map<string, Date>(),

    cachedPosts: new Map<string, Post>(),
    specificLoading: new Map<string, boolean>,
    lastCaching: new Map<string, Date>,

    loading: false,
    error: false,
  }),
  actions: {

    /**
     * Hydrates the store by requesting the posts of the given event uuid from the API.
     * @param eventUuid the uuid of the event associated with requested posts
     */
    async hydrateSpecific(eventUuid: string) {
      this.loading = true;
      this.error = false;
      try {
        const { data } = await postApi.getOf(eventUuid);
        let postsOfEvent = data as Post[];
        postsOfEvent.forEach(p => {
          this.cachedPosts.set(p.uuid, p);
          this.lastCaching.set(p.uuid, new Date());
        });
        this.postsOfEvents.set(
          eventUuid,
          postsOfEvent.map(p => p.uuid)
        );
        this.lastSuccessfulHydration.set(eventUuid, new Date());
      } catch (e) {
        console.warn(`Failed to fetch posts for event with id ${eventUuid}.`, e);
        this.error = true;
      }
      this.loading = false;
    },

    /**
     * Retrieves all posts associated with the event with given uuid.
     * Initializes the retrieval of the posts if they have not yet been loaded.
     * @param eventUuid the uuid of the event associated with requested posts
     * @returns an array of posts
     */
    getPostsOf(eventUuid: string) {
      const requestedPosts = this.postsOfEvents.get(eventUuid);
      const lastUpdate = this.lastSuccessfulHydration.get(eventUuid);
      if (
        requestedPosts === undefined
        || lastUpdate !== undefined && new Date().getTime() - lastUpdate.getTime() > STORES.CACHE_MAX_AGE
      ) {
        this.hydrateSpecific(eventUuid);
      }
      return requestedPosts?.map(pUuid => this.cachedPosts.get(pUuid) as Post) || [] as Post[];
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
        this.hydrateSpecific(eventUuid);
      }
      return requestedPost || {} as Post;
    },

    /**
     * Changes the cached post to the given one.
     * If one of `post` or `post.uuid` is `undefined` no action is taken.
     * @param post the event
     */
    setEvent(post: Post|undefined) {
      if (post === undefined || post.uuid === undefined) return;
      this.cachedPosts.set(post.uuid, post);
    },

    getEventGetter(uuid: string, eventUuid: string) {
      return computed({
        get: () => this.getPost(uuid, eventUuid),
        set: (e) => this.setEvent(e),
      });
    },
  },
})
