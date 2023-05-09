/**
 * Service to control all stores.
 * Partly taken from Directus' hydrate.ts.
 * @author David Maier
 * @since Weekly Build 1
 * @see https://github.com/directus/directus/blob/main/app/src/hydrate.ts
 */

import {useAppStore} from "@/store/app";
import {useAccountStore} from "@/store/account";
import {useOrganizationStore} from "@/store/organizations";
import {usePostStore} from "@/store/posts";
import {useQuestionnaireStore} from "@/store/questionnaires";
import {useEventStore} from "@/store/events";
import {StoreDefinition} from "pinia";

/**
 * Use-definitions of all stores that should be hydrated on startup.
 * The stores will be hydrated in the given order (if they have a `hydrate`-function implemented).
 */
const stores = [
  useAppStore,
  useAccountStore,
  useOrganizationStore,
  useEventStore,
  usePostStore,
  useQuestionnaireStore,
] as StoreDefinition[];

type GenericStore = {
  hydrate?: () => Promise<void>,
  requestHydration?: () => Promise<void>,
  dehydrate?: () => Promise<void>,

  [key: string]: any;
};

/**
 * Clears all stores that have a dehydration-function.
 */
export async function dehydrateAll() {
  for (let useStore of stores) {
    const store = useStore() as GenericStore;
    await store.dehydrate?.();
  }
}

/**
 * Forces all stores that have hydration-function to hydrate.
 * **Note**: Preferably call `requestHydrationAll()` to use cache advantages.
 */
export async function hydrateAll() {
  for (let useStore of stores) {
    const store = useStore() as GenericStore;
    await store.hydrate?.();
  }
}

/**
 * Requests all stores that have requestHydration-function to hydrate if necessary.
 * Uses advantages of caching instead of forcing all stores to update at once.
 */
export async function requestHydrateAll() {
  for (let useStore of stores) {
    const store = useStore() as GenericStore;
    await store.requestHydration?.();
  }
}
