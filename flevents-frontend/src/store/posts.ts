import { defineStore } from 'pinia'
// import postsApi from "@/api/postsApi";
import {Post} from "@/models/post";

export const usePostStore = defineStore('posts', {
  state: () => ({
    posts: new Map<string, Post[]>(),
    lastSuccessfulHydration: new Map<string, Date>(),
    loading: false,
    error: false,
  }),
  actions: {

    /**
     * Hydrates the store by requesting the posts of the given event uuid from the api.
     * @param eventUuid the uuid of the event associated with requested posts
     */
    async hydrate(eventUuid: string) {
      this.loading = true;
      this.error = false;
      // TODO: create and use posts store
      try {
        // const { data } = await postsApi.getPosts(eventUuid);
        // this.posts.set(eventUuid, data as Post[]);
        // this.lastSuccessfulHydration.set(eventUuid, new Date());
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
    getPosts(eventUuid: string) {
      const requestedPosts = this.posts.get(eventUuid);
      const lastUpdate = this.lastSuccessfulHydration.get(eventUuid);
      if (
        requestedPosts === undefined
        || lastUpdate !== undefined && new Date().getTime() - lastUpdate.getTime() > 60000
      ) {
        this.hydrate(eventUuid);
      }
      return requestedPosts || [] as Post[];
    },
  },
})
