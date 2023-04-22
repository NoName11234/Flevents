/**
 * Service to control all stores.
 * Partly taken from Directus' hydrate.ts.
 * @author David Maier
 * @see https://github.com/directus/directus/blob/main/app/src/hydrate.ts
 */

import {useAppStore} from "@/store/app";
import {useAccountStore} from "@/store/account";
import {useOrganizationStore} from "@/store/organizations";
import {usePostStore} from "@/store/posts";
import {useSurveyStore} from "@/store/surveys";
import {useEventStore} from "@/store/events";
import {StoreDefinition} from "pinia";

const stores = [
  useAppStore,
  useAccountStore,
  useOrganizationStore,
  usePostStore,
  useSurveyStore,
  useEventStore,
] as StoreDefinition[];

type GenericStore = {
  hydrate?: () => Promise<void>,
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
